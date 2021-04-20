package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.capstone.fasttiffin.databinding.ActivityPhoneAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : BaseActivity() {
    private lateinit var binding: ActivityPhoneAuthBinding

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        

        auth = FirebaseAuth.getInstance()




        val currentUser = auth.currentUser
        if(currentUser != null) {
            startActivity(Intent(applicationContext,DashboardActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener{
            login()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                hideProgressDialog()
                val intent = Intent(this@PhoneAuthActivity,DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                hideProgressDialog()
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                showProgressDialog()
                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token


                val mobileNumber=binding.phoneNumber
                val number=mobileNumber.text.toString().trim(){it <= ' '}.toLong()
                val intent = Intent(applicationContext,PhoneAuthVerifyActivity::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("storedPhoneNo",number)
                startActivity(intent)
            }
        }


    }


    private fun login() {
        val mobileNumber=binding.phoneNumber
        var number=mobileNumber.text.toString().trim(){it <= ' '}

        if(number.isNotEmpty()){
            number= "+91$number"
            sendVerificationcode (number)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        showProgressDialog()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}