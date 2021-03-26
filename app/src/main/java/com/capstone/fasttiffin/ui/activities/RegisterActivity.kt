package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityRegisterBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener{
            registerUser()
        }
    }

    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarRegisterActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Validating New Users
    private fun validateRegisterDetails(): Boolean{
        return when{
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_first_name),true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_last_name),true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email),true)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches() ->{
                showErrorSnackBar(resources.getString(R.string.invalid_email),true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password),true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_password),true)
                false
            }
            binding.etPassword.text.toString().trim{ it <= ' '} != binding.etConfirmPassword.text.toString()
                .trim{ it <= ' '} -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_password_confirm_password_mismatch),true)
                false
                }
            !binding.cbTermsAndConditions.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_conditions),true)
                false
            }
            else -> {
                // showErrorSnackBar(resources.getString(R.string.successfull_register), false)
                true
            }
        }
    }

    // Sending data of users to firebase
    private fun registerUser(){

    //Check with validate function if the entries are valid or not
        if(validateRegisterDetails())
        {
            showProgressDialog()

            val userEmail: String = binding.etEmail.text.toString().trim{ it <= ' '}
            val userPassword: String = binding.etPassword.text.toString().trim{ it <= ' '}

            // Create an instance and register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener{ task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {
                            //Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Email Verification

                            val user = User(
                                firebaseUser.uid,
                                binding.etFirstName.text.toString().trim{ it <= ' '},
                                binding.etLastName.text.toString().trim{ it <= ' '},
                                binding.etEmail.text.toString().trim{ it <= ' '}
                            )

                            FirestoreClass().registerUser(this, user)

                            //It will throw you to the login activity
//                            FirebaseAuth.getInstance().signOut()
//                            finish()
                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }

        }
    }
    //Firebase Firestore
    fun userRegistrationSuccess(){
        //Hide the progress dialog
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.register_success),
            Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500)
    }
}