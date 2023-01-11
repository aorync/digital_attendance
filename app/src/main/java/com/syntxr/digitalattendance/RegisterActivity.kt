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
import com.syntxr.digitalattendance.databinding.ActivityRegisterBinding
import com.syntxr.digitalattendance.data.model.register.Register
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val preferences = getSharedPreferences(ObjectKey.PREF_NAME,Context.MODE_PRIVATE)

        binding.btnRegisterToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.inputRegisterName.text.toString()
            val email = binding.inputRegisterEmail.text.toString()
            val address = binding.inputRegisterAddress.text.toString()
            val phoneNumber = binding.inputRegisterPhone.text.toString()
            val password = binding.inputRegisterPassword.text.toString()
            val confirm = binding.inputRegisterConfirm.text.toString()

            if (name.isEmpty()) {
                binding.inputRegisterName.error = "Nama harus diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.inputRegisterEmail.error = "Email harus diisi"
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                binding.inputRegisterAddress.error = "Alamat harus diisi"
                return@setOnClickListener
            }

            if (phoneNumber.isEmpty()) {
                binding.inputRegisterName.error = "Nomor telpon harus diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.inputRegisterName.error = "Password harus diisi"
                return@setOnClickListener
            }

            if (confirm != password) {
                binding.inputRegisterConfirm.error = "Password tidak sama"
                return@setOnClickListener
            }

            val encryptedPassword = EncryptUtils.encryptMD5ToString(password)

            val register = Register(
                name = name,
                address = address,
                email = email,
                password = encryptedPassword,
                phoneNumber = phoneNumber
            )

            binding.registerProgress.visibility = View.VISIBLE

            val api = Objects.create()
            lifecycleScope.launch {
                val rensponse = api.registerUser(register)
                if (rensponse.isEmpty()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Gagal Menginput Data",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                binding.registerProgress.visibility = View.GONE

                preferences.edit().putInt(ObjectKey.ID_LOGIN,(rensponse.first().id)).apply()
                preferences.edit().putString(ObjectKey.EMAIL_LOGIN,(rensponse.first().email)).apply()
                preferences.edit().putString(ObjectKey.NAME_LOGIN,(rensponse.first().name)).apply()

                preferences.edit().putInt(ObjectKey.ID_UPDATE,(rensponse.first().id)).apply()
                preferences.edit().putString(ObjectKey.NAME_UPDATE,(rensponse.first().name)).apply()
                preferences.edit().putString(ObjectKey.EMAIL_UPDATE,(rensponse.first().email)).apply()
                preferences.edit().putString(ObjectKey.ADDRESS_UPDATE,(rensponse.first().address)).apply()
                preferences.edit().putString(ObjectKey.PHONE_UPDATE,(rensponse.first().phoneNumber)).apply()
                preferences.edit().putString(ObjectKey.PASSWORD_UPDATE,(rensponse.first().password)).apply()


                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}