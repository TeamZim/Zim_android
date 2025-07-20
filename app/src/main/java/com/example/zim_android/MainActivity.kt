package com.example.zim_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Context // ← 요거 꼭 필요!
import android.os.Bundle
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import android.content.pm.PackageManager


class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private var gotoFragmentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        printKeyHash(this)


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

    private fun printKeyHash(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )

            info.signingInfo?.apkContentsSigners?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", keyHash) // ← 복사해서 Kakao Developers 콘솔에 등록
            }

        } catch (e: Exception) {
            Log.e("KeyHashError", "키 해시 생성 실패", e)
        }
    }

}



