package com.capstone.fasttiffin.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.utils.Constants

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar!!.setBackgroundDrawable(
                ContextCompat.getDrawable(this@DashboardActivity,R.drawable.app_gradient_color_background)
        )

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navViewAdmin: BottomNavigationView = findViewById(R.id.nav_view_admin)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if(FirestoreClass().getCurrentUserID() == Constants.ADMIN){
            navView.visibility = View.GONE
            navViewAdmin.visibility = View.VISIBLE
            val appBarConfiguration = AppBarConfiguration(
                    setOf(
                            R.id.navigation_products, R.id.navigation_dashboard
                    )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navViewAdmin.setupWithNavController(navController)
        }
        else{
            navView.visibility = View.VISIBLE
            navViewAdmin.visibility = View.GONE
            val appBarConfiguration = AppBarConfiguration(
                    setOf(
                            R.id.navigation_dashboard, R.id.navigation_orders
                    )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}