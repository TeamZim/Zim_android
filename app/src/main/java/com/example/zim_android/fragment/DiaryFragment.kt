package com.example.zim_android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zim_android.Adapter.DiaryAdapter
import com.example.zim_android.R
import com.example.zim_android.data.model.DiaryImageResponse
import com.example.zim_android.data.model.DiaryResponse
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DiaryPageBinding

class DiaryFragment: Fragment(R.layout.diary_page) {
    private var _binding: DiaryPageBinding? = null
    private val binding get() = _binding!!

    val userId = UserSession.userId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DiaryPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 나중에 api 연결 필요한 부분들
        binding.backBtnHeader.tvTitle.text = "여행명"
        binding.backBtnHeader.backBtn.setOnClickListener {
            // 뒤로 가기 작업.
        }

        // 인터페이스 작동만 볼거라 더미 리스트 생성해둠.
        val dummyList = List(10) { DiaryResponse(
            id = 1,
            tripId = 1,
            countryName = "",
            city = "",
            dateTime = "",
            content = "",
            detailedLocation = "",
            audioUrl = "",
            emotionColor = "",
            weather = "",
            images = listOf(
                DiaryImageResponse(
                    id = 1,
                    imageUrl = "https://bucket.s3.amazonaws.com/image1.jpg",
                    cameraType = "FRONT",
                    isRepresentative = true,
                    imageOrder = 1
                ),
                DiaryImageResponse(
                    id = 2,
                    imageUrl = "https://bucket.s3.amazonaws.com/image2.jpg",
                    cameraType = "BACK",
                    isRepresentative = false,
                    imageOrder = 2
                )
            ),
            tripName = "",
            createdAt = ""
        ) }

        val diaryItemAdapter = DiaryAdapter(dummyList)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.adapter = diaryItemAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}