package com.syntxr.digitalattendance.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.permissionx.guolindev.PermissionX
import com.syntxr.digitalattendance.R
import com.syntxr.digitalattendance.UsersActivity
import com.syntxr.digitalattendance.`object`.ObjectKey
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.FragmentDashboardBinding
import com.syntxr.digitalattendance.data.model.attendance.AttendanceIn
import com.syntxr.digitalattendance.data.model.attendance.AttendanceOut
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding: FragmentDashboardBinding by viewBinding()
    private val CHAPTURE_PHOTO = 1
    private var uris : Uri? = null
    private val storage = Firebase.storage
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latidude: Double? = null
    private var longitude: Double? = null
    private var imageUrl: String? = null

    private var latitudes : Double? = null
    private var longitudes : Double? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())


//        SmartLocation.with(requireContext()).location().state().locationServicesEnabled()
//        SmartLocation.with(requireContext()).location().state().isAnyProviderAvailable
//        SmartLocation.with(requireContext()).location().state().isGpsAvailable
//        SmartLocation.with(requireContext()).location().state().isNetworkAvailable
//        SmartLocation.with(requireContext()).location().state().isPassiveAvailable

        fetchData()

        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val name = preferences.getString(ObjectKey.NAME_LOGIN, "")
        val image = preferences.getString(ObjectKey.IMG_ATTENDANCE, "")

        binding.imgAttendance.load("$image")

        binding.uploadPhotoAttendance.setOnClickListener {
            openCamera()
        }
//        getLocation()
        trackLocation()

        binding.cardCheckIn.setOnClickListener {
            attendanceIn()
        }

        binding.cardCheckOut.setOnClickListener {
            attendanceOuts()
        }


        binding.swipeDashboard.setOnRefreshListener {
            fetchData()
        }

        binding.btnViewAllUser.setOnClickListener {
            val intent = Intent(requireContext(), UsersActivity::class.java)
            startActivity(intent)
        }

        binding.tvUsername.text = name
        binding.imgUserProfile.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profil)
        }
    }

    fun attendanceOuts() {

        val attendanceOut = AttendanceOut(
            out = "now()",
            latitude = latidude,
            longitude = longitude
        )
        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_LOGIN, 0)
        val api = Objects.create()

        lifecycleScope.launch {
            try {
                val checkInExist = api.CheckAttendanceExist("eq.$id", "eq.now()")

                if (checkInExist.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "anda belum absen pulang hari ini",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (checkInExist.first().out != null) {
                    Toast.makeText(
                        requireContext(),
                        "Anda telah absen pulang hari ini",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val rensponseOut = api.attendOut("eq.$id", "eq.now()", attendanceOut)


                if (rensponseOut.isEmpty()) {
                    Toast.makeText(requireContext(), "gagal absen keluar", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }
                Toast.makeText(requireContext(), "berhasil absen keluar", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }


    }

    fun attendanceIn() {


        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_LOGIN, 0)
        val api = Objects.create()
        val image = preferences.getString(ObjectKey.IMG_ATTENDANCE, "")!!
        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Anda belum memilih photo", Toast.LENGTH_SHORT).show()
        } else {
            val attendanceIn = AttendanceIn(
                date = "now()",
                inx = "now()",
                out = null,
                userId = id,
                latitudeIn = latitudes,
                longitudeIn = longitudes,
                imgUrl = imageUrl.toString(),
                latitudeOut = null,
                longitudeOut = null
            )

            lifecycleScope.launch {
                try {

                    val checkInExist = api.CheckAttendanceExist("eq.$id", "eq.now()")


                    if (checkInExist.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Anda telah absen hari ini",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val rensponseIn = api.attendIn(attendanceIn)


                    if (rensponseIn.isEmpty()) {
                        Toast.makeText(requireContext(), "gagal absen masuk", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }
                    Toast.makeText(requireContext(), "berhasil absen masuk", Toast.LENGTH_SHORT)
                        .show()


                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    }

    fun fetchData() {

        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_LOGIN, 0)
        val api = Objects.create()



        lifecycleScope.launch {
            try {

                val checkInShimmer = binding.tvCheckIn.createSkeleton()
                val checkOutShimmer = binding.tvCheckOut.createSkeleton()
                val usernameShimmer = binding.tvUsername.createSkeleton()
                val imgUser = binding.imgUserProfile.createSkeleton()
                val imgAttendance = binding.imgAttendance.createSkeleton()

                checkInShimmer.showSkeleton()
                checkOutShimmer.showSkeleton()
                imgUser.showSkeleton()
                usernameShimmer.showSkeleton()
                imgAttendance.showSkeleton()

                val checkInClock = api.getCheckInHistory("eq.$id", "*", "eq.now()")
                val profil = api.profilUser("eq.$id", "*")

                checkInShimmer.showOriginal()
                imgUser.showOriginal()
                checkOutShimmer.showOriginal()
                usernameShimmer.showOriginal()
                imgAttendance.showOriginal()

//                onDataLoad()

                binding.swipeDashboard.isRefreshing = false

//                binding.tvCheckIn.createSkeleton().showOriginal()
//                binding.tvCheckOut.createSkeleton().showOriginal()
//                binding.tvUsername.createSkeleton().showOriginal()


                binding.tvUsername.text = profil.first().name
                binding.imgUserProfile.load(profil.first().imgUrl)

//                check In

//                format dateTime

                if (checkInClock.isNotEmpty()) {

                    binding.uploadPhotoAttendance.visibility = View.GONE

                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    val format = SimpleDateFormat("HH:mm")

                    binding.imgAttendance.load(checkInClock.first().imgUrl)

                    if (checkInClock.first().inX.isNullOrEmpty()) {
                        binding.tvCheckIn.text = "--:--"
                    } else {
                        val formatDateIn = format.format(parser.parse(checkInClock.first().inX)!!)
                        binding.tvCheckIn.text = formatDateIn
                    }

                    if (checkInClock.first().out.isNullOrEmpty()) {
                        binding.tvCheckOut.text = "--:--"



                    } else {
                        val formatDateOut = format.format(parser.parse(checkInClock.first().out)!!)
                        binding.tvCheckOut.text = formatDateOut
                    }
                    return@launch
                }


            } catch (e: Exception) {
                Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun onDataLoad() {
        binding.tvCheckIn.createSkeleton().showOriginal()
        binding.tvCheckOut.createSkeleton().showOriginal()
        binding.tvUsername.createSkeleton().showOriginal()
    }

    private fun openCamera() {
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    val media = result?.first() ?: return
                    val file = File(media.availablePath)
                    uploadPhoto(file)
                }

                override fun onCancel() {

                }

            })
    }

    private fun uploadPhoto(file: File) {
        val preferences =
            requireContext().getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_UPDATE, 0)
        storage.getReference("images")
            .child("attendance")
            .child("$id")
            .child(file.name)
            .putFile(file.toUri())
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toast.makeText(requireContext(), "Gagal Upload File", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
                it.result.storage.downloadUrl.addOnSuccessListener {
                    val image = it.toString()
                    imageUrl = image
                    preferences.edit().putString(ObjectKey.IMG_ATTENDANCE, image)
                    binding.imgAttendance.load(image)
                }
            }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                latidude = it.latitude
                longitude = it.longitude
            }
        }
    }

    private fun trackLocation(){
        val locationTracker = LocationTracker(
            5 * 6 * 1000,
            100f,
            shouldUseGPS = true,
            shouldUseNetwork = true,
            shouldUsePassive = true
        )


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationTracker.startListening(requireActivity())

        PermissionX.init(requireActivity()).permissions(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION).request{
                allGranted, _, deniedAccess ->
            if (allGranted){
                locationTracker.addListener(object : LocationTracker.Listener{
                    override fun onLocationFound(location: Location) {
                        latitudes = location.latitude
                        longitudes = location.longitude
                    }

                    override fun onProviderError(providerError: ProviderError) {
                    }
                })
                return@request
            }

            Toast.makeText(requireContext(), "Izin Akses Ditolak : $deniedAccess", Toast.LENGTH_SHORT).show()


        }
    }

//    private fun openSystemCamera(){
//        val captureImg = File(requireContext().externalCacheDir,"Photo.jpg")
//        if (captureImg.exists()){
//            captureImg.delete()
//        }
//        captureImg.createNewFile()
//        uris = if (Build.VERSION.SDK_INT >= 24){
//            FileProvider.getUriForFile(requireActivity(),"digital attendance",captureImg)
//        }else{
//            Uri.fromFile(captureImg)
//        }
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,uris)
//        startActivityForResult(intent,CHAPTURE_PHOTO)
//
//    }




}