package com.example.zim_android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.zim_android.View.OnBoardingActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전체화면 설정 (상태바 없애기)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!PreferenceManager.isOnboardingShown(this)) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))

            }
            finish()
        }, 2000)

    }
}
