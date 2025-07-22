package com.example.zim_android.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.Adapter.DiaryAdapter
import com.example.zim_android.Adapter.DiaryAdapter.DiaryViewHolder
import com.example.zim_android.R
import com.example.zim_android.data.model.DiaryResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DiaryPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class DiaryFragment : Fragment(R.layout.diary_page) {
    private var _binding: DiaryPageBinding? = null
    private val binding get() = _binding!!

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

        binding.backBtnHeader.tvTitle.text = "여행명"
        binding.backBtnHeader.backBtn.setOnClickListener {
            binding.backBtnHeader.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        val userId = UserSession.userId
        if (userId == null) {
            Log.e("DiaryFragment", "User ID is null")
            return
        }

        api.getDiariesByUser(userId)
            .enqueue(object : Callback<List<DiaryResponse>> {
                override fun onResponse(
                    call: Call<List<DiaryResponse>>,
                    response: Response<List<DiaryResponse>>
                ) {
                    Log.d("DiaryFragment", "현재 userId: $userId")

                    Log.d("DiaryFragment", "응답 수신 - 성공 여부: ${response.isSuccessful}")
                    Log.d("DiaryFragment", "응답 바디: ${response.body().toString()}")

                    if (response.isSuccessful) {
                        val diaryList = response.body() ?: emptyList()

                        Log.d("DiaryFragment", "다이어리 개수: ${diaryList.size}")
                        diaryList.forEachIndexed { index, diary ->
                            Log.d("DiaryFragment", "[$index] Diary ID: ${diary.id}")
                        }

                        val diaryAdapter = DiaryAdapter(diaryList)
                        Log.d("DiaryFragment", "RecyclerView 어댑터 설정 완료")
                        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
                        binding.recyclerView.adapter = diaryAdapter
                        Log.d("DiaryFragment", "RecyclerView 레이아웃매니저 설정 완료")


                        val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
                        binding.recyclerView.post {
                            val targetId = arguments?.getInt("diaryId")
                            targetId?.let { id ->
                                val index = diaryList.indexOfFirst { it.id == id }
                                if (index != -1) {
                                    val itemHeight = binding.recyclerView.getChildAt(0)?.height ?: 0
                                    val recyclerViewHeight = binding.recyclerView.height
                                    val offset = (recyclerViewHeight / 2) - (itemHeight / 2)

                                    layoutManager.scrollToPositionWithOffset(index, offset)
                                }
                            }
                        }
                        
                    }
                }

                override fun onFailure(call: Call<List<DiaryResponse>>, t: Throwable) {
                    Log.e("DiaryFragment", "API 요청 실패: ${t.message}", t)
                }
            })

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // 스크롤이 시작될 때 녹음 재생 멈추기
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    DiaryAdapter.stopPlayingAudioIfNeeded()
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
