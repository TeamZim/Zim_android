package com.example.zim_android.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.RectF
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
                binding.textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                binding.textRight.setTextColor(Color.parseColor("#000000"))
                binding.cardViewpager.visibility = View.GONE
                binding.mapContainer.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            } else {
                binding.textLeft.setTextColor(Color.parseColor("#000000"))
                binding.textRight.setTextColor(Color.parseColor("#A7A4A0"))
                binding.cardViewpager.visibility = View.VISIBLE
                binding.mapContainer.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        // ViewPager2 설정
        val dummyCards = listOf("카드1", "카드2", "카드3", "카드4")
        val adapter = CardAdapter(dummyCards)

        val viewPager = binding.cardViewpager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val seekBar = binding.progressBar
        seekBar.max = dummyCards.size - 1

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

        val btnGroup = binding.editButtonGroup
        val btnCancel = binding.btnCancel
        val btnSave = binding.btnSave

        btnCancel.setOnClickListener {
            adapter.setFocusCard(null)
            binding.dimOverlay.visibility = View.GONE
            btnGroup.visibility = View.GONE
        }

        btnSave.setOnClickListener {
            // 저장 로직 처리 (필요 시 추가)
            adapter.setFocusCard(null)
            binding.dimOverlay.visibility = View.GONE
            btnGroup.visibility = View.GONE
        }

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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is GridLayoutManager) {
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    (recyclerView.adapter as? PhotoGridAdapter)?.setLastVisibleIndex(lastVisible)
                }
            }
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewPager.currentItem = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        adapter.setOnEditClickListener(object : CardAdapter.OnEditClickListener {
            override fun onEditButtonClicked(position: Int) {
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
                    Log.d("DEBUG", "✅ dimOverlay set to VISIBLE")
                }

            }
        })

        adapter.setOnCardEditFieldClickListener(object : CardAdapter.OnCardEditFieldClickListener {
            override fun onTitleClick(position: Int) {
                val dialog = Record_Modify_1(
                    currentTitle = "현재 제목",
                    onTitleUpdated = { newTitle ->
                        Log.d("Edit", "새 제목: $newTitle")
                        // 데이터 업데이트 로직
                    }
                )
                dialog.show(parentFragmentManager, "editTitle")
            }


            override fun onDateClick(position: Int) {
                Log.d("Edit", "날짜 클릭됨 at $position")
            }

            override fun onMemoClick(position: Int) {
                val dialog = Record_Modify_2(currentTitle = "현재 제목",
                    onTitleUpdated = { newMemo ->
                        Log.d("Edit", "새 제목: $newMemo")
                        Log.d("Edit", "메모 클릭됨 at $position")

                }
                )
                dialog.show(parentFragmentManager, "editMemo")

            }


            override fun onImageClick(position: Int) {
                val photoSelectDialog = Dialog(requireContext()) // 커스텀 다이얼로그 객체 생성
                val photoSelectDialogBinding = DialogSelectPhotoBinding.inflate(layoutInflater) // 뷰를 코드로 가지고와서 이제 객체를 얘를 통해 받아오면됨.
                photoSelectDialog.setContentView(photoSelectDialogBinding.root) // 다이얼로그의 UI를 XML과 연결

                photoSelectDialogBinding.exitBtn.setOnClickListener {
                    photoSelectDialog.dismiss()
                }

                // 사진이 선택된 경우 처리해야함.
                // + 저장 버튼 활성화하기
                photoSelectDialogBinding.saveBtn.setOnClickListener {
                    //
                }

                ApiProvider.api.getTripRepresentativeImages(tripId)
                    .enqueue(object : Callback<List<TripImageResponse>> {
                        override fun onResponse(
                            call: Call<List<TripImageResponse>>,
                            response: Response<List<TripImageResponse>>
                        ) {
                            if (response.isSuccessful) {
                                val imageList = response.body() ?: emptyList()
                                val photoSelectAdapter = DialogPhotoSelectAdapter(requireContext(), imageList){
                                    selectedItem ->
                                    val selectedImg: String = selectedItem.imageUrl
                                }
                                // 이 부분에서 updateTrip 추가
                                // api.updateTrip()
                                photoSelectDialogBinding.gridView.adapter = photoSelectAdapter
                            } else {
                                Log.e("API", "응답 실패: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<List<TripImageResponse>>, t: Throwable) {
                            Log.e("API", "네트워크 오류: ${t.message}")
                        }
                    })
                Log.d("Edit", "사진 클릭됨 at $position")



                photoSelectDialog.show()
            }


    })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
