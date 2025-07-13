package com.example.zim_android.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CardItemDecoration
import com.example.zim_android.Adapter.CardAdapter
import com.example.zim_android.Adapter.PhotoGridAdapter
import com.example.zim_android.data.model.TripResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.ViewCardFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewCardFragment : Fragment(R.layout.view_card_fragment) {

    private var _binding: ViewCardFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ViewCardFragmentBinding.bind(view)

        // 헤더 타이틀 설정
        binding.commonHeader.tvTitle.text = "모음"

        // 스위치 스타일 설정
        binding.switch1.apply {
            thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.slide_thumb)
            trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)
        }

        // 지도 프래그먼트 연결
        val mapContainerId = binding.mapContainer.id
        val fragment = childFragmentManager.findFragmentById(mapContainerId)
        if (fragment == null) {
            childFragmentManager.beginTransaction()
                .replace(mapContainerId, ViewMapFragment())
                .commit()
        }

        // 스위치 변경 시 UI 토글
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 맵 보기
                binding.textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                binding.textRight.setTextColor(Color.parseColor("#000000"))
                binding.cardViewpager.visibility = View.GONE
                binding.mapContainer.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            } else {
                // 카드 보기
                binding.textLeft.setTextColor(Color.parseColor("#000000"))
                binding.textRight.setTextColor(Color.parseColor("#A7A4A0"))
                binding.cardViewpager.visibility = View.VISIBLE
                binding.mapContainer.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        // 유저 ID 가져오기 (기본값 1)
        val userId = UserSession.userId ?: 1

        // 사용자 여행 목록 받아오기
        api.getTripsByUser(userId).enqueue(object : Callback<List<TripResponse>> {
            override fun onResponse(
                call: Call<List<TripResponse>>,
                response: Response<List<TripResponse>>
            ) {
                if (response.isSuccessful) {
                    val tripList = response.body() ?: emptyList()

                    val imageMap = mutableMapOf<Long, List<TripImageResponse>>()
                    var loadedCount = 0

                    // 각 trip의 대표 이미지 불러오기
                    for (trip in tripList) {
                        getRepresentativeImages(trip.id) { images ->
                            imageMap[trip.id.toLong()] = images
                            loadedCount++

                            if (loadedCount == tripList.size) {
                                setupCardAdapter(tripList, imageMap)
                            }
                        }
                    }
                } else {
                    Log.e("API", "여행 목록 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TripResponse>>, t: Throwable) {
                Log.e("API", "네트워크 오류: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 대표 이미지 가져오는 함수
    private fun getRepresentativeImages(
        tripId: Int,
        callback: (List<TripImageResponse>) -> Unit
    ) {
        ApiProvider.api.getTripRepresentativeImages(tripId).enqueue(object : Callback<List<TripImageResponse>> {
            override fun onResponse(
                call: Call<List<TripImageResponse>>,
                response: Response<List<TripImageResponse>>
            ) {
                if (response.isSuccessful) {
                    val images = response.body() ?: emptyList()
                    callback(images)
                } else {
                    Log.e("API", "대표사진 응답 실패: ${response.code()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<TripImageResponse>>, t: Throwable) {
                Log.e("API", "대표사진 네트워크 오류: ${t.message}")
                callback(emptyList())
            }
        })
    }

    // 카드 어댑터 설정 및 ViewPager 연결
    private fun setupCardAdapter(
        tripList: List<TripResponse>,
        imageMap: Map<Long, List<TripImageResponse>>
    ) {
        val adapter = CardAdapter(tripList, imageMap)
        val viewPager = binding.cardViewpager
        val seekBar = binding.progressBar

        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        seekBar.max = tripList.size - 1

        // 카드 넘기기 감지해서 시크바에 반영
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var lastPosition = -1
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (lastPosition != -1 && lastPosition != position) {
                    adapter.resetFlip(lastPosition)
                }
                lastPosition = position
                seekBar.progress = position
            }
        })

        // ViewPager 양쪽 패딩과 애니메이션 효과
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        recyclerView.setPadding(80, 0, 80, 0)
        viewPager.offscreenPageLimit = 3
        viewPager.addItemDecoration(CardItemDecoration(19))
        viewPager.setPageTransformer { page, position ->
            page.scaleY = 0.95f
            page.translationX = -40 * position
        }

        // 시크바로 카드 넘기기
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewPager.currentItem = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 카드 수정 버튼 클릭 시 Fragment 이동
        adapter.setOnEditClickListener(object : CardAdapter.OnEditClickListener {
            override fun onEditButtonClicked(position: Int) {
                val selectedTrip = tripList[position]
                val fragment = Record_Modify_Fragment.newInstance(selectedTrip)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}
