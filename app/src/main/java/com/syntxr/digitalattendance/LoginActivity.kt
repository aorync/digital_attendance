package com.syntxr.digitalattendance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.EncryptUtils
import com.syntxr.digitalattendance.`object`.ObjectKey
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.ActivityLoginBinding
import com.syntxr.digitalattendance.data.model.login.LoginRensponseItem
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding : ActivityLoginBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val preferences = getSharedPreferences(ObjectKey.PREF_NAME,Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_LOGIN,0)


        binding.btnLoginToRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString()
            val password = binding.inputLoginPassword.text.toString()

            if (email.isEmpty()){
                binding.inputLoginEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()){
                binding.inputLoginPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            val encryptedPassword = EncryptUtils.encryptMD5ToString(password)

            binding.loginProgress.visibility = View.VISIBLE

            val api = Objects.create()
            lifecycleScope.launch {
                val loginUser = api.loginUser("*","eq.$email","eq.$encryptedPassword",)
                if (loginUser.isEmpty()){
                    Toast.makeText(this@LoginActivity, "Email atau Password anda salah", Toast.LENGTH_SHORT).show()
                    binding.loginProgress.visibility = View.GONE
                    return@launch
                }

                binding.loginProgress.visibility = View.GONE

                preferences.edit().putInt(ObjectKey.ID_LOGIN,(loginUser.first().id)).apply()
                preferences.edit().putString(ObjectKey.EMAIL_LOGIN,(loginUser.first().email)).apply()
                preferences.edit().putString(ObjectKey.NAME_LOGIN,(loginUser.first().name)).apply()

                preferences.edit().putInt(ObjectKey.ID_UPDATE,(loginUser.first().id)).apply()
                preferences.edit().putString(ObjectKey.NAME_UPDATE,(loginUser.first().name)).apply()
                preferences.edit().putString(ObjectKey.EMAIL_UPDATE,(loginUser.first().email)).apply()
                preferences.edit().putString(ObjectKey.ADDRESS_UPDATE,(loginUser.first().address)).apply()
                preferences.edit().putString(ObjectKey.PHONE_UPDATE,(loginUser.first().phoneNumber)).apply()
                preferences.edit().putString(ObjectKey.PASSWORD_UPDATE,(loginUser.first().password)).apply()


                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val preferences = getSharedPreferences(ObjectKey.PREF_NAME,Context.MODE_PRIVATE)
        val id = preferences.getInt(ObjectKey.ID_LOGIN,0)
        if (id != 0){
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }
}