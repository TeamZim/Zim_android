package com.example.zim_android.View

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProcessor
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import com.example.zim_android.Adapter.DialogEmotionSelectAdapter
import com.example.zim_android.R
import com.example.zim_android.data.model.Emotion
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.databinding.DialogSelectEmotionColorBinding
import com.example.zim_android.databinding.DialogSelectWeatherBinding
import com.example.zim_android.databinding.Record4Binding
import com.example.zim_android.databinding.ViewMapDialog1Binding

class Record_4_Activity : AppCompatActivity() {

    private lateinit var binding: Record4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Record4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 헤더 설정
        binding.backBtnHeader.tvTitle.text = "기록하기"
        binding.backBtnHeader.backBtn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // 전달받은 값
        val imagePath1 = intent.getStringExtra("imagePath1")
        val imagePath2 = intent.getStringExtra("imagePath2")
        val representIndex = intent.getIntExtra("representIndex", 0)

        // 이미지 세팅
        if (!imagePath1.isNullOrEmpty()) {
            val bitmap1 = BitmapFactory.decodeFile(imagePath1)
            binding.image1.setImageBitmap(bitmap1)
        }
        if (!imagePath2.isNullOrEmpty()) {
            val bitmap2 = BitmapFactory.decodeFile(imagePath2)
            binding.image2.setImageBitmap(bitmap2)
        }

        //글자 수 세는
        binding.placeInput.addTextChangedListener {
            val count = it?.length ?: 0
            binding.placeCounter.text = "$count/16"
        }


        // 대표 라벨 처리
        binding.labelRepresent1.visibility = if (representIndex == 0) View.VISIBLE else View.GONE
        binding.labelRepresent2.visibility = if (representIndex == 1) View.VISIBLE else View.GONE

        //감정색 선택 다이얼로그
        val emotionButton = findViewById<LinearLayout>(R.id.emotion_button)

        val context = this // 이렇게 미리 정의해두고
        binding.emotionButton.setOnClickListener {
            showEmotionColorDialog(this)
        }

        binding.weatherButton.setOnClickListener {
            showWeatherDialog(this)
        }






    }

    private var selectedWeatherIcon: ImageView? = null

    private fun showWeatherDialog(context: Context) {
        val dialog = Dialog(context)
        val dialogBinding = DialogSelectWeatherBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val weatherMap = mapOf(
            dialogBinding.weatherSunny to Pair("맑음", R.drawable.weather_sunny_sel),
            dialogBinding.weatherCloudy to Pair("흐림", R.drawable.weather_cloud_sel),
            dialogBinding.weatherRainy to Pair("비", R.drawable.weather_rainy_sel),
            dialogBinding.weatherWindy to Pair("바람", R.drawable.weather_windy_sel),
            dialogBinding.weatherSnow to Pair("눈", R.drawable.weather_snowy_sel)
        )

        // 초기 확인 버튼 비활성화
       dialogBinding.btnConfirm.isEnabled = false


        // 아이콘 클릭 시
        for ((icon, data) in weatherMap) {
            icon.setOnClickListener {
                // 기존 선택된 아이콘 원래 이미지로 되돌림
                selectedWeatherIcon?.setImageResource(getUnselectedRes(selectedWeatherIcon?.id))

                // 새 아이콘 선택 표시
                icon.setImageResource(data.second)
                selectedWeatherIcon = icon

                // 텍스트 변경
                binding.weatherText.text = data.first
                binding.weatherCircle.setImageResource(data.second)


                // 확인 버튼 활성화
                dialogBinding.btnConfirm.isEnabled = true
                dialogBinding.btnConfirm.setBackgroundResource(R.drawable.save_btn_active)
            }
        }

        dialogBinding.btnBack.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnConfirm.setOnClickListener { dialog.dismiss() }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


}


private fun showEmotionColorDialog(context: Context) {

    val dialog = Dialog(context)
    val dialogBinding = DialogSelectEmotionColorBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(dialogBinding.root)

    // 닫기 버튼 클릭 시 다이얼로그 닫기
    dialogBinding.backToDialog1.setOnClickListener {
        dialog.dismiss()
    }

    // 저장 버튼 클릭 시 다이얼로그 닫기 (데이터 처리 없이)
    dialogBinding.dialog2SaveBtn.setOnClickListener {
        dialog.dismiss()
    }

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.show()
}



// 선택 해제용 이미지 반환 함수
private fun getUnselectedRes(viewId: Int?): Int {
    return when (viewId) {
        R.id.weather_sunny -> R.drawable.weather_sunny_unsel
        R.id.weather_cloudy -> R.drawable.weather_cloud_unsel
        R.id.weather_rainy -> R.drawable.weather_rainy_unsel
        R.id.weather_windy -> R.drawable.weather_windy_unsel
        R.id.weather_snow -> R.drawable.weather_snowy_unsel
        else -> R.drawable.weather_sunny_unsel
    }
}


