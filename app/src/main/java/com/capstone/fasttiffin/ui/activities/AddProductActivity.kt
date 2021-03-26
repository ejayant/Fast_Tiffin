package com.capstone.fasttiffin.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityAddProductBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader
import java.io.IOException


class AddProductActivity : BaseActivity() {
    private lateinit var binding: ActivityAddProductBinding

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.ivAddUpdateProduct.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (validateProductDetails()) {
                uploadProductImage()
            }
        }
    }

    private fun uploadProductImage(){
        showProgressDialog()
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.PRODUCT_IMAGE)
    }

    // Back Button Code In Toolbar
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                showErrorSnackBar("The storage permission is granted.",false)
                Constants.showImageChooser(this)
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                    mSelectedImageFileUri = data.data!!
                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivProductImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }
            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.product_uploaded_success_message),
        Toast.LENGTH_SHORT).show()
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {
//            hideProgressDialog()
//            showErrorSnackBar("Product image is uploaded successfully. Image URL: $imageURL", false)
            mProductImageURL = imageURL
        uploadProductDetails()
        }

    fun uploadProductDetails(){
        val username = this.getSharedPreferences(
                Constants.FASTTIFFIN_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME,"")!!

        val product = Product(
                FirestoreClass().getCurrentUserID(),
                username,
                binding.etProductTitle.text.toString().trim{ it <= ' '},
                binding.etProductPrice.text.toString().trim{ it <= ' '},
                binding.etProductDescription.text.toString().trim{ it <= ' '},
                binding.etProductQuantity.text.toString().trim{ it <= ' '},
                mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this, product)
    }
}



