package com.capstone.fasttiffin.ui.activities


import android.content.Intent
import android.os.Bundle
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivitySettingsBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.User
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity() {

    private lateinit var mUserDetails: User
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,LoginActivity::class.java)
            //It will clear all the activities or fragments which we have opened before.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.tvEdit.setOnClickListener {
            val intent = Intent(this,UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        }
    }

    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSettingsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails(){
        showProgressDialog()
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User){
        mUserDetails = user
        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)
        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

}