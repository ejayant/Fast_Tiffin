package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityLoginBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.User
import com.capstone.fasttiffin.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            logInRegisteredUser()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser(){

        //Check with validate function if the entries are valid or not
        if(validateLoginDetails())
        {
            showProgressDialog()

            val userEmail: String = binding.etEmail.text.toString().trim{ it <= ' '}
            val userPassword: String = binding.etPassword.text.toString().trim{ it <= ' '}

            // Create an instance and register a user with email and password
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener{ task ->

                    if (task.isSuccessful) {
                            FirestoreClass().getUserDetails(this)
                    }
                    else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    //Firebase Firestore
    fun userLoggedInSuccess(user: User){
        hideProgressDialog()

        if(user.profileCompleted == 0){
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        }
        else {
            //Redirect the user to the main screen after login
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }
}