package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityPhoneAuthVerifyBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.User
import com.capstone.fasttiffin.utils.Constants
import com.google.firebase.auth.*

class PhoneAuthVerifyActivity : BaseActivity() {
    private lateinit var binding: ActivityPhoneAuthVerifyBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        val storedVerificationId=intent.getStringExtra("storedVerificationId")


//        Reference
        val verify=findViewById<Button>(R.id.verifyBtn)
        val otpGiven=findViewById<EditText>(R.id.id_otp)


        verify.setOnClickListener{
            val otp=otpGiven.text.toString().trim()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showProgressDialog()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {


                    val storedPhoneNo = intent.getLongExtra("storedPhoneNo", 0)


                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    if(task.getResult()?.additionalUserInfo?.isNewUser == true) {
                        val user = User(
                            firebaseUser.uid,
                            "",
                            "",
                            "",
                            "",
                            storedPhoneNo
                        )
                        FirestoreClass().registerUser(this, user)
                        FirestoreClass().getUserDetails(this)
                    }else {
                        FirestoreClass().getUserDetails(this)
                    }

                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        if (user.profileCompleted == 0) {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }


    fun userRegistrationSuccess(){
        hideProgressDialog()

    }

}