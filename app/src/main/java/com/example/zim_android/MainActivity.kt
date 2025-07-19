package com.example.zim_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private var gotoFragmentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        handleNavigationIntent(intent)
    }

    private fun handleNavigationIntent(intent: Intent?) {
        val fragmentName = intent?.getStringExtra("gotoFragment")
        if (fragmentName == "ViewCard") {
            navController.popBackStack(R.id.viewCardFragment, false)
            navController.navigate(R.id.viewCardFragment)
            Log.d("MainActivity", "받은 이동 요청: $fragmentName")
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume 호출됨 / gotoFragmentName=$gotoFragmentName")
        if (gotoFragmentName == "ViewCard") {
            navController.navigate(R.id.viewCardFragment)
            gotoFragmentName = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavigationIntent(intent)
    }





}



