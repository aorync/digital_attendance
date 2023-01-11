package com.syntxr.digitalattendance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.syntxr.digitalattendance.`object`.ObjectKey
import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.databinding.ActivityUsersBinding
import com.syntxr.digitalattendance.ui.UsersAdapter
import kotlinx.coroutines.launch

class UsersActivity : AppCompatActivity() {

    private val binding: ActivityUsersBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        binding.userAppBar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.userAppBar.toolbar.setTitle("Users")
        binding.userAppBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val preferences = getSharedPreferences(ObjectKey.PREF_NAME, Context.MODE_PRIVATE)

        fetchData()

    }

    private fun fetchData() {
        val api = Objects.create()
        lifecycleScope.launch {
            try {

                val skeleton = binding.rvListUser.applySkeleton(R.layout.item_users)
                skeleton.showSkeleton()
                val listUsers = api.allUser("*")
                skeleton.showOriginal()

                val adapter = UsersAdapter(listUsers)
                val layoutManager = LinearLayoutManager(this@UsersActivity)
                binding.rvListUser.adapter = adapter
                binding.rvListUser.layoutManager = layoutManager

                adapter.ItemClick = {
                    val intent = Intent(this@UsersActivity, UpdateUsersActivity::class.java)
                    intent.putExtra(ObjectKey.ID_SEND, (it.id))
                    intent.putExtra(ObjectKey.NAME_SEND, (it.name))
                    intent.putExtra(ObjectKey.EMAIL_SEND, (it.email))
                    intent.putExtra(ObjectKey.ADDRESS_SEND, (it.address))
                    intent.putExtra(ObjectKey.PHONE_SEND, (it.phoneNumber))
                    intent.putExtra(ObjectKey.PASSWORD_SEND, (it.password))
                    startActivity(intent)
                }

                adapter.ItemDelete = {
                    MaterialAlertDialogBuilder(this@UsersActivity)
                        .setTitle("Hapus User")
                        .setMessage("Anda yakin ingin menghapus user ini")
                        .setPositiveButton("Iya") { dialog, which ->

                            binding.deleteProgress.visibility = View.VISIBLE

                            val api = Objects.create()

                            lifecycleScope.launch {
                                try {
                                    api.deleteUser("eq.${it.id}")

                                    binding.deleteProgress.visibility = View.GONE

                                    Toast.makeText(
                                        this@UsersActivity,
                                        "Hapus data berhasil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@UsersActivity, this@UsersActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } catch (e: Exception) {
                                    val intent =
                                        Intent(this@UsersActivity, this@UsersActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            }
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }.show()


                }
            } catch (e: Exception) {
                Toast.makeText(this@UsersActivity, "error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}