package com.capstone.fasttiffin.ui.activities

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.fasttiffin.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressOnce = false
    private lateinit var mProgressDialog: Dialog

    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view

        if(errorMessage){
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarError)
            )
        }
        else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarSuccess)
            )
        }
        snackbar.show()
    }

    fun showProgressDialog(){
        mProgressDialog = Dialog(this)
        /* Set a screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen. */
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){

        if(doubleBackToExitPressOnce){
            super.onBackPressed()
            return
        }

        doubleBackToExitPressOnce = true
        Toast.makeText(this,resources.getString(R.string.double_click_back),Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressOnce = false
        },2000)

    }
}