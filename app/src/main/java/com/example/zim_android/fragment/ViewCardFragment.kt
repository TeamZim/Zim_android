package com.example.zim_android.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CardItemDecoration
import com.example.zim_android.Adapter.CardAdapter
import com.example.zim_android.Adapter.DialogPhotoSelectAdapter
import com.example.zim_android.Adapter.PhotoGridAdapter
import com.example.zim_android.data.model.TripResponse
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.data.network.ApiProvider
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DialogSelectPhotoBinding
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

        // 헤더 설정
        binding.commonHeader.tvTitle.text = "모음"

        // 스위치 설정
        binding.switch1.apply {
            thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.slide_thumb)
            trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)
        }

        // mapContainer에 ViewMapFragment xml 연결
        val mapContainerId = binding.mapContainer.id
        val fragment = childFragmentManager.findFragmentById(mapContainerId)
        if (fragment == null) {
            childFragmentManager.beginTransaction()
                .replace(mapContainerId, ViewMapFragment())
                .commit()
        }

        // 스위치 전환 색 변환
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

        val userId = UserSession.userId ?: 1
        // 사용자 ID
        //아직 카카오로그인이 안된 상태라 노션에 적혀진 1로 하긴 했는데 나중에 카카오로그인 후에 수정필요

        // 사용자 여행 목록 받아오기
        ApiProvider.api.getTripsByUser(userId).enqueue(object : Callback<List<TripResponse>> {
            override fun onResponse(
                call: Call<List<TripResponse>>,
                response: Response<List<TripResponse>>
            ) {
                if (response.isSuccessful) {
                    val tripList = response.body() ?: emptyList()
                    val adapter = CardAdapter(tripList)
                    val viewPager = binding.cardViewpager
                    val seekBar = binding.progressBar


                    //카드 뷰 ViewPager2 설정
                    //뷰페이저2란 머냐면 안드로이드에서 좌우(또는 상하)로 스와이프하면서 여러 페이지(뷰)를 넘길 수 있게 해주는 UI 컴포넌트래
                    viewPager.adapter = adapter
                    viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    seekBar.max = tripList.size - 1

                    // ViewPager 페이지 이동 시 seekBar 연동 + 카드 뒤집기 초기화
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

                    // 카드 간 여백 및 슬라이드 효과 적용
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

                    // 마지막 보인 그리드 이미지 감도 적용 (PhotoGridAdapter에 index 전달)
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            val layoutManager = recyclerView.layoutManager
                            if (layoutManager is GridLayoutManager) {
                                val lastVisible = layoutManager.findLastVisibleItemPosition()
                                (recyclerView.adapter as? PhotoGridAdapter)?.setLastVisibleIndex(lastVisible)
                            }
                        }
                    })

                    // SeekBar 이동 시 카드 변경
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            if (fromUser) {
                                viewPager.currentItem = progress
                            }
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })

                    // 수정 버튼 클릭 시 카드 강조 + 딤 오버레이 표시 -> 수정 필요 진짜 죽인다
                    //안되서 걍 프래그먼트 연결로 다시 만듬 하아
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
}

///여기서 수정하기 화면 프래그먼트로 만들기
///수정하기 버튼 조정 필요