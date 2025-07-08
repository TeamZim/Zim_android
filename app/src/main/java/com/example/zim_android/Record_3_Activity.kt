package com.example.zim_android

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zim_android.View.Record_4_Activity
import com.example.zim_android.databinding.Record3Binding

class Record_3_Activity: AppCompatActivity() {
   // private var is_represent: Boolean = true // 데이터 구조 만들어서 처리해야함.

    private lateinit var binding: Record3Binding  // ViewBinding 선언
    private var selectedRepresentativeIndex: Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_3)

        binding = Record3Binding.inflate(layoutInflater) // 반드시 먼저 초기화
        setContentView(binding.root)                     // View 연결

        binding.backBtnHeader.tvTitle.text = "기록하기"
        // 헤더의 백버튼 클릭 시
        binding.backBtnHeader.backBtn.setOnClickListener {
            val intent = Intent(this@Record_3_Activity, Record_2_1_Activity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Intent 속 이미지 경로 가져오기
        val imagePath1 = intent.getStringExtra("imagePath1")
        val imagePath2 = intent.getStringExtra("imagePath2")

        // 이미지 띄우기
        if (!imagePath1.isNullOrEmpty()) {
            val bitmap1 = BitmapFactory.decodeFile(imagePath1)
            binding.imageView1.setImageBitmap(bitmap1)
        }

        if (!imagePath2.isNullOrEmpty()) {
            val bitmap2 = BitmapFactory.decodeFile(imagePath2)
            binding.imageView2.setImageBitmap(bitmap2)
        }

        // 대표사진 버튼 처리
        binding.isRepresentBtn1.setOnClickListener {
            selectedRepresentativeIndex = 0
            updateRepresentIndicators()
        }
        binding.isRepresentBtn2.setOnClickListener {
            selectedRepresentativeIndex = 1
            updateRepresentIndicators()
        }

        // 대표사진 1로 우선 선택해두기
        updateRepresentIndicators()


        // 다시 촬영 버튼 클릭 시
        binding.retakeBtn.setOnClickListener {
            val intent = Intent(this@Record_3_Activity, Record_2_1_Activity::class.java) // 카메라 촬영 화면으로 돌아가기
            // 처음에 일기 관련 정보 같이 넘겨주기 추가해야함.싀엣
            startActivity(intent)
            finish()
            //
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this, Record_4_Activity::class.java)
            intent.putExtra("imagePath1", imagePath1)
            intent.putExtra("imagePath2", imagePath2)
            intent.putExtra("representIndex", selectedRepresentativeIndex)
            startActivity(intent)
        }


        // 일기로 넘기기
        // 일기 저장을 해야 사진이 저장되도록하기 위해 여기서는 경로만 보냄
//        val intent = Intent(this@Record_3_Activity, 일기화면::class.java)
//        intent.putExtra("imagePath1", imagePath1)
//        intent.putExtra("imagePath2", imagePath2)
//        intent.putExtra("is_represent_1", ~) // 이미지를 내보낼 때 데이터 클래스 만들어야할 것 같음.
//        startActivity(intent)


    }



    private fun updateRepresentIndicators() {
        val selectedImg = R.drawable.selected_img_btn
        val unselectedImg = R.drawable.unselected_img_btn

        binding.isRepresentBtn1.setImageResource(if (selectedRepresentativeIndex == 0) selectedImg else unselectedImg)
        binding.isRepresentBtn2.setImageResource(if (selectedRepresentativeIndex == 1) selectedImg else unselectedImg)
    }

//    // 이전으로 돌아갈 때도
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//    }
}