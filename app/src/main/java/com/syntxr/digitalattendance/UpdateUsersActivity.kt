package com.syntxr.digitalattendance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.EncryptUtils
import com.syntxr.digitalattendance.`object`.ObjectKey
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.ActivityUpdateUsersBinding
import com.syntxr.digitalattendance.data.model.update.Update
import kotlinx.coroutines.launch

class UpdateUsersActivity : AppCompatActivity() {

    private val binding : ActivityUpdateUsersBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_users)

        binding.updateAppBar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.updateAppBar.toolbar.title = "Update Profil"
        binding.updateAppBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val preferences = getSharedPreferences(ObjectKey.PREF_NAME,Context.MODE_PRIVATE)

        val id = intent.getIntExtra(ObjectKey.ID_SEND,0)
        val name = intent.getStringExtra(ObjectKey.NAME_SEND)
        val email = intent.getStringExtra(ObjectKey.EMAIL_SEND)
        val address = intent.getStringExtra(ObjectKey.ADDRESS_SEND)
        val phone = intent.getStringExtra(ObjectKey.PHONE_SEND)
        val passwords = intent.getStringExtra(ObjectKey.PASSWORD_SEND)

        binding.editUserName.setText(name)
        binding.editUserEmail.setText(email)
        binding.editUserAddress.setText(address)
        binding.editUserPhone.setText(phone)


        binding.btnUpdateUser.setOnClickListener {

            val name = binding.editUserName.text.toString()
            val email = binding.editUserEmail.text.toString()
            val address = binding.editUserAddress.text.toString()
            val phone = binding.editUserPhone.text.toString()
            val password = binding.editUserPassword.text.toString()

            if (name.isEmpty()){
                binding.editUserName.error = "Nama harus diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()){
                binding.editUserEmail.error = "Email harus diisi"
                return@setOnClickListener
            }

            if (address.isEmpty()){
                binding.editUserAddress.error = "Alamat harus diisi"
                return@setOnClickListener
            }

            if (phone.isEmpty()){
                binding.editUserPhone.error = "Nomor telepon harus diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()){
//                binding.editUserPassword.error = "Password harus diisi"
                binding.editUserPassword.setText(passwords)
                return@setOnClickListener
            }

            val encryptedPassword = EncryptUtils.encryptMD5ToString(password)

            val update = Update(
                name = name,
                email = email,
                address = address,
                phoneNumber = phone,
                password = encryptedPassword
            )

            binding.updateProgress.visibility = View.VISIBLE

            val api = Objects.create()
            lifecycleScope.launch {
                val update = api.updateUser("eq.$id", update)
                if (update.isEmpty()){
                    Toast.makeText(this@UpdateUsersActivity, "gagal update data", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                binding.updateProgress.visibility = View.GONE
                onBackPressed()
            }
        }


    }
}