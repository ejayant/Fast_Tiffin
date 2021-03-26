package com.capstone.fasttiffin.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityUserProfileBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.User
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            // Get the user details from intent as a ParcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstName.setText(mUserDetails.firstName)
        binding.etLastName.setText(mUserDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)

        if(mUserDetails.profileCompleted == 0){
            binding.tvTitle.text = resources.getString(R.string.title_profile)
            binding.etFirstName.isEnabled = false

            binding.etLastName.isEnabled = false
        }
        else{
            setupActionBar()
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this).loadUserPicture(mUserDetails.image, binding.ivUserPhoto)
            if(mUserDetails.mobile != 0L){
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if(mUserDetails.gender == Constants.MALE){
                binding.rbMale.isChecked = true
            }
            else{
                binding.rbFemale.isChecked = true
            }
        }

        binding.ivUserPhoto.setOnClickListener {
            // Here we will check if the permission is already allowed or we need to request for it.
            // First of all we will check the READ_EXTERNAL_STORAGE permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
//                showErrorSnackBar("You already have the storage permission.", false)
                Constants.showImageChooser(this)
            }
            else{
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding.btnSave.setOnClickListener {

            if(validateUserProfileDetails()){
                showProgressDialog()
                // Uploading image to cloud storage
                if(mSelectedImageFileUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                }
                else{
                    updateUserProfileDetails()
                }
            }

        }
    }

    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarUserProfileActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()
        val firstName = binding.etFirstName.text.toString().trim{ it <= ' '}
        if(firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = binding.etLastName.text.toString().trim{ it <= ' '}
        if(lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim{ it <= ' '}
        val gender = if(binding.rbMale.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if(mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }


    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.update_success),
            Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            // If permission is granted
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showErrorSnackBar("The storage permission is granted.",false)
                Constants.showImageChooser(this)
            }
            else{
                // Displaying another toast if permission is not granted
                Toast.makeText(this,resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!
                        //One way to use without glide
                        // binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,binding.ivUserPhoto)
                    }
                    catch(e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this,resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled", "Image Selection Cancelled")
        }
    }


    // Mobile Number Field
    fun validateUserProfileDetails(): Boolean{
        return when{
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile_number),true)
                false
            }
            else -> {
                true
            }
        }
    }


    fun imageUploadSuccess(imageURL: String){
//        hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}