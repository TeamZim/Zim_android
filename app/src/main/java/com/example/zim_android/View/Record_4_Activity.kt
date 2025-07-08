package com.example.zim_android.View

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.zim_android.R
import com.example.zim_android.databinding.Record4Binding

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

        // 완료 버튼 클릭 리스너 (아직 저장 기능은 미구현)
       // binding.completeBtn.setOnClickListener {
            // TODO: 장소명, 감정, 날씨, 소음, 기록 내용을 가져와서 저장하는 로직 작성
        }
    }

