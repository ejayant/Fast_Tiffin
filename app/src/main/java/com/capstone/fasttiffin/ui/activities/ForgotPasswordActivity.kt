package com.capstone.fasttiffin.ui.activities

import android.os.Bundle
import android.widget.Toast
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.btnSubmit.setOnClickListener {
            val userEmail: String = binding.etEmailForgot.text.toString().trim{ it <= ' '}
            if(userEmail.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_email),true)
            }
            else{
                showProgressDialog()
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if(task.isSuccessful){
                            Toast.makeText(this,resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                        else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }
        }
    }
    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarForgotPasswordActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }
    }

}