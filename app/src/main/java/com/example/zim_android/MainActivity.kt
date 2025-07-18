package com.example.zim_android

import android.content.Intent
import android.os.Bundle
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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        // 👉 Intent 값 저장만 하고, 실제 이동은 onResume에서 처리
        gotoFragmentName = intent?.getStringExtra("gotoFragment")
    }

    override fun onResume() {
        super.onResume()

        if (gotoFragmentName == "ViewCard") {
            // ✅ 현재 fragment가 viewCardFragment가 아닌 경우에만 이동
            if (navController.currentDestination?.id != R.id.viewCardFragment) {
                navController.navigate(R.id.viewCardFragment)
            }

            // ✅ 바텀탭도 선택되도록 보장
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.selectedItemId = R.id.menu_view_card

            gotoFragmentName = null
        }
    }

}



