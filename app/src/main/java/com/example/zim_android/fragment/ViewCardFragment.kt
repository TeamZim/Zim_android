package com.example.zim_android.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsAnimation
import android.widget.SeekBar
import androidx.camera.core.ImageProcessor
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

    val tripId: Int = UserSession.currentTripId?.toInt()!!

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
                    adapter.setOnEditClickListener(object : CardAdapter.OnEditClickListener {
                        override fun onEditButtonClicked(position: Int) {
                            // 카드 수정 모드 진입 (버튼 그룹 보여주기 등)
                            adapter.setFocusCard(position)
                            binding.editButtonGroup.visibility = View.VISIBLE

                            val recyclerView = binding.cardViewpager.getChildAt(0) as? RecyclerView
                            val holder = recyclerView?.findViewHolderForAdapterPosition(position)
                            val cardView = holder?.itemView ?: return

                            val location = IntArray(2)
                            cardView.getLocationOnScreen(location)

                            val hole = RectF(
                                location[0].toFloat(),
                                location[1].toFloat(),
                                (location[0] + cardView.width).toFloat(),
                                (location[1] + cardView.height).toFloat()
                            )

                            binding.dimOverlay.post {
                                binding.dimOverlay.visibility = View.VISIBLE
                            }
                        }
                    })

                    // 각 필드 수정 버튼 클릭 처리
                    adapter.setOnCardEditFieldClickListener(object : CardAdapter.OnCardEditFieldClickListener {
                        override fun onTitleClick(position: Int) {
                            val dialog = Record_Modify_1(
                                currentTitle = "현재 제목", // 실제 tripList[position].title 등으로 바꿔도 됨
                                onTitleUpdated = { newTitle ->
                                    Log.d("Edit", "새 제목: $newTitle")
                                    // TODO: API로 업데이트 등 처리
                                }
                            )
                            dialog.show(parentFragmentManager, "editTitle")
                        }

                        override fun onDateClick(position: Int) {
                            Log.d("Edit", "날짜 클릭됨 at $position")
                            // TODO: 날짜 수정 다이얼로그 띄우기 등
                        }

                        override fun onMemoClick(position: Int) {
                            val dialog = Record_Modify_2(
                                currentTitle = "현재 메모",
                                onTitleUpdated = { newMemo ->
                                    Log.d("Edit", "새 메모: $newMemo")
                                }
                            )
                            dialog.show(parentFragmentManager, "editMemo")
                        }

                        override fun onImageClick(position: Int) {
                            // 사진 선택 다이얼로그 띄우기
                            val dialog = Dialog(requireContext())
                            val bindingDialog = DialogSelectPhotoBinding.inflate(layoutInflater)
                            dialog.setContentView(bindingDialog.root)

                            bindingDialog.exitBtn.setOnClickListener {
                                dialog.dismiss()
                            }

                            // 여행 대표 이미지 리스트 가져오기-> db에 사진이 없는건지 내가 잘못 불러온건지 확인필요
                            ApiProvider.api.getTripRepresentativeImages(tripId)
                                .enqueue(object : Callback<List<TripImageResponse>> {
                                    override fun onResponse(
                                        call: Call<List<TripImageResponse>>,
                                        response: Response<List<TripImageResponse>>
                                    ) {
                                        val imageList = response.body() ?: emptyList()
                                        val photoAdapter = DialogPhotoSelectAdapter(requireContext(), imageList) { selectedItem ->
                                            Log.d("Image", "선택된 이미지: ${selectedItem.imageUrl}")
                                        }
                                        bindingDialog.gridView.adapter = photoAdapter
                                    }

                                    override fun onFailure(call: Call<List<TripImageResponse>>, t: Throwable) {
                                        Log.e("API", "이미지 불러오기 실패: ${t.message}")
                                    }
                                })

                            dialog.show()
                        }
                    })

                } else {
                    Log.e("API", "여행 목록 응답 실패: ${response.code()}")
                }
            }
        })


        adapter.setOnPhotoClickListener(object : CardAdapter.OnPhotoClickListener {
            override fun onPhotoClick(cardPosition: Int, imagePosition: Int) {
                Log.d("ImageClick", "cardPos=$cardPosition, imagePos=$imagePosition")
                findNavController().navigate(R.id.action_viewCardFragment_to_diaryFragment)
            }
        })

            override fun onFailure(call: Call<List<TripResponse>>, t: Throwable) {
                Log.e("API", "네트워크 오류: ${t.message}")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


///여기서 수정하기 화면 프래그먼트로 만들기
///수정하기 버튼 조정 필요