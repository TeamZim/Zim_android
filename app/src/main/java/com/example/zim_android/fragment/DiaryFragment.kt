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
            findNavController().navigate(R.id.action_diaryFragment_to_viewCardFragment)
            // 그냥 네비게이션으로 넘기는게 아니라,따로 무슨 카드였는지를 같이 보내줘야함.
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
                    if (response.isSuccessful) {
                        val diaryList = response.body() ?: emptyList()
                        val diaryAdapter = DiaryAdapter(diaryList)
                        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
                        binding.recyclerView.adapter = diaryAdapter

                        binding.recyclerView.post {
                            val targetId = arguments?.getInt("diaryId")
                            targetId?.let { id ->
                                val index = diaryList.indexOfFirst { it.id == id }
                                if (index != -1) {
                                    binding.recyclerView.scrollToPosition(index)
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
