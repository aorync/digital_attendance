package com.syntxr.digitalattendance.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.syntxr.digitalattendance.LoginActivity
import com.syntxr.digitalattendance.R
import com.syntxr.digitalattendance.UpdateUsersActivity
import com.syntxr.digitalattendance.`object`.ObjectKey
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.FragmentProfilBinding
import com.syntxr.digitalattendance.data.model.attendance.ImageAttendance
import com.syntxr.digitalattendance.data.model.users.ImageProfile
import kotlinx.coroutines.launch
import java.io.File


class ProfilFragment : Fragment(R.layout.fragment_profil) {

    private val binding: FragmentProfilBinding by viewBinding()
    private val storage = Firebase.storage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val email = preferences.getString(ObjectKey.EMAIL_LOGIN, "")
        val name = preferences.getString(ObjectKey.NAME_LOGIN, "")

        val nameUpdate = preferences.getString(ObjectKey.NAME_UPDATE, "")
        val emailUpdate = preferences.getString(ObjectKey.EMAIL_UPDATE, "")
        val address = preferences.getString(ObjectKey.ADDRESS_UPDATE, "")
        val phone = preferences.getString(ObjectKey.PHONE_UPDATE, "")
        val password = preferences.getString(ObjectKey.PASSWORD_UPDATE, "")
        val image = preferences.getString(ObjectKey.IMG_PROFILE,"")

        val id = preferences.getInt(ObjectKey.ID_UPDATE, 0)



        binding.swipeProfil.setOnRefreshListener {
            fetchData()
        }

        binding.imgUpload.setOnClickListener {
            openAlbum()
        }

//        val nameUpdate = preferences.getString(ObjectKey.NAME_UPDATE,"")
//        val emailUpdate = preferences.getString(ObjectKey.EMAIL_UPDATE,"")
//        val address = preferences.getString(ObjectKey.ADDRESS_UPDATE,"")
//        val phone = preferences.getString(ObjectKey.PHONE_UPDATE,"")
//        val password = preferences.getString(ObjectKey.PASSWORD_UPDATE,"")


        binding.btnUpdate.setOnClickListener {
            val intent = Intent(requireContext(), UpdateUsersActivity::class.java)
            intent.putExtra(ObjectKey.ID_SEND, id)
            intent.putExtra(ObjectKey.NAME_SEND, nameUpdate)
            intent.putExtra(ObjectKey.EMAIL_SEND, emailUpdate)
            intent.putExtra(ObjectKey.ADDRESS_SEND, address)
            intent.putExtra(ObjectKey.PHONE_SEND, phone)
            intent.putExtra(ObjectKey.PASSWORD_SEND, password)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            preferences.edit().remove(ObjectKey.ID_LOGIN).apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun openAlbum() {
        PictureSelector.create(this)
            .openSystemGallery(SelectMimeType.ofAll())
            .setSelectMaxFileSize(2)
            .forSystemResultActivity(object : OnResultCallbackListener<LocalMedia?>{
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    val media = result?.first()?:return
                    val file = File(media.availablePath)
//                    binding.imgUser.load(media.availablePath)
                    uploadFile(file)
                }

                override fun onCancel(){
                }

            })
    }

    private fun imageCamera() {
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {}
                override fun onCancel() {}
            })
    }

    private fun uploadFile(file: File){
        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_UPDATE, 0)
        val userId = id
        storage.getReference("images")
            .child("$userId")
            .child(file.name)
            .putFile(file.toUri())
            .addOnCompleteListener{
                if (!it.isSuccessful){
                    Toast.makeText(requireContext(), "Gagal Upload File", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                it.result.storage.downloadUrl.addOnSuccessListener {
//                    ketika berhasil upload dan tersimpan di database
                    val api = Objects.create()

                    val imageUrl = it.toString()
                    binding.imgUser.load(imageUrl)
                    preferences.edit().putString(ObjectKey.IMG_PROFILE,imageUrl).apply()
                    val imageProfile = ImageProfile(
                        imgUrl = imageUrl
                    )
                    lifecycleScope.launch {
                        try {
                            val uploadImg = api.uploadImageProfile("eq.$userId",imageProfile)

                            if (uploadImg.isEmpty()){
                                Toast.makeText(requireContext(), "Gagal Upload gambar", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                        }catch (e : Exception){
                        }

                    }
                }
            }


    }

    private fun fetchData(){
        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val nameUpdate = preferences.getString(ObjectKey.NAME_UPDATE, "")
        val emailUpdate = preferences.getString(ObjectKey.EMAIL_UPDATE, "")
        val id = preferences.getInt(ObjectKey.ID_UPDATE, 0)
        val api = Objects.create()

        lifecycleScope.launch {
            try {

                val emailShimmer = binding.tvEmailUser.createSkeleton()
                val usernameShimmer = binding.tvUserName.createSkeleton()
                val imageShimmer = binding.imgUser.createSkeleton()

                emailShimmer.showSkeleton()
                usernameShimmer.showSkeleton()
                imageShimmer.showSkeleton()

                val rensponse = api.profilUser("eq.$id", "*")
                val profil = rensponse.first()
                binding.swipeProfil.isRefreshing = false
                binding.tvEmailUser.text = profil.email
                binding.tvUserName.text = profil.name
                binding.imgUser.load("${profil.imgUrl}")

                emailShimmer.showOriginal()
                usernameShimmer.showOriginal()
                imageShimmer.showOriginal()




                preferences.edit().putInt(ObjectKey.ID_UPDATE, (profil.id)).apply()
                preferences.edit().putString(ObjectKey.NAME_UPDATE, (profil.name)).apply()
                preferences.edit().putString(ObjectKey.EMAIL_UPDATE, (profil.email)).apply()
                preferences.edit().putString(ObjectKey.ADDRESS_UPDATE, (profil.address)).apply()
                preferences.edit().putString(ObjectKey.PHONE_UPDATE, (profil.phoneNumber)).apply()
                preferences.edit().putString(ObjectKey.PASSWORD_UPDATE, (profil.password)).apply()

            }catch (e : Exception){
                Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT).show()
                binding.tvUserName.text = nameUpdate
                binding.tvEmailUser.text = emailUpdate
            }
        }
    }


//    override fun onStart() {
//        super.onStart()
//
//        val api = Objects.create()
//        lifecycleScope.launch {
//            val profil = api.profilUser("eq.$id", "*")
//            binding.tvEmailUser.text = profil.first().email
//            binding.tvUserName.text = profil.first().name
//        }
//
//    }
}