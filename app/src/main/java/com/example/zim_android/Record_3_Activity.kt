package com.example.zim_android

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.zim_android.View.Record_4_Activity
import com.example.zim_android.data.network.DiaryTempStore
import com.example.zim_android.databinding.Record3Binding

class Record_3_Activity: AppCompatActivity() {
    private lateinit var binding: Record3Binding
    private var selectedRepresentativeIndex: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_3)

        binding = Record3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnHeader.tvTitle.text = "기록하기"

        // 헤더의 백버튼 클릭 시
        binding.backBtnHeader.backBtn.setOnClickListener {
            val intent = Intent(this@Record_3_Activity, Record_2_1_Activity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }


        val imagePath1 = DiaryTempStore.images.getOrNull(0)?.imageUrl
        val imagePath2 = DiaryTempStore.images.getOrNull(1)?.imageUrl

        if (!imagePath1.isNullOrEmpty()) {
            val bitmap1 = BitmapFactory.decodeFile(imagePath1)
            binding.imageView1.setImageBitmap(bitmap1)
        }
        if (!imagePath2.isNullOrEmpty()) {
            val bitmap2 = BitmapFactory.decodeFile(imagePath2)
            binding.imageView2.setImageBitmap(bitmap2)
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this, Record_4_Activity::class.java)
            intent.putExtra("tripId", getIntent().getIntExtra("tripId", 0))
            intent.putExtra("imagePath1", imagePath1)
            intent.putExtra("imagePath2", imagePath2)
            intent.putExtra("representIndex", selectedRepresentativeIndex)
            startActivity(intent)
        }

        // 대표사진 1로 우선 선택해두기
        updateRepresentIndicators()

        // 대표사진 버튼 처리
        binding.isRepresentBtn1.setOnClickListener {
            selectedRepresentativeIndex = 0
            updateRepresentIndicators()
        }
        binding.isRepresentBtn2.setOnClickListener {
            selectedRepresentativeIndex = 1
            updateRepresentIndicators()
        }


        // 다시 촬영 버튼 클릭 시
        binding.retakeBtn.setOnClickListener {
            val tripId = intent.getIntExtra("tripId", 0)
            val intent = Intent(this@Record_3_Activity, Record_2_1_Activity::class.java)
            intent.putExtra("tripId", tripId)
            startActivity(intent)
            finish()

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.nextBtn.setOnClickListener {
            val tripId = intent.getIntExtra("tripId", 0)
            val intent = Intent(this, Record_4_Activity::class.java)
            intent.putExtra("tripId", tripId)
            intent.putExtra("imagePath1", imagePath1)
            intent.putExtra("imagePath2", imagePath2)
            intent.putExtra("representIndex", selectedRepresentativeIndex)
            startActivity(intent)
        }
    }



    private fun updateRepresentIndicators() {
        val selectedImg = R.drawable.selected_img_btn
        val unselectedImg = R.drawable.unselected_img_btn

        binding.isRepresentBtn1.setImageResource(if (selectedRepresentativeIndex == 0) selectedImg else unselectedImg)
        binding.isRepresentBtn2.setImageResource(if (selectedRepresentativeIndex == 1) selectedImg else unselectedImg)

        // DiaryTempStore에 저장된 representative 바꾸기
        val updatedImages = DiaryTempStore.images.mapIndexed { index, image ->
            image.copy(representative = index == selectedRepresentativeIndex) // 대표 이미지 선택한 것만 true
        }
        DiaryTempStore.images = updatedImages

        Log.d("DiaryTempStore", DiaryTempStore.toString())

    }

//    // 이전으로 돌아갈 때도
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//    }
}