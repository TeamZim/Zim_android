package com.example.zim_android.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.zim_android.Adapter.CardAdapter
import com.example.zim_android.Adapter.DialogPhotoSelectAdapter
import com.example.zim_android.R
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.data.model.TripResponse
import com.example.zim_android.data.network.ApiProvider
import com.example.zim_android.databinding.DialogSelectPhotoBinding
import com.example.zim_android.databinding.RecordModifyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.zim_android.data.network.UserSession


class Record_Modify_Fragment : Fragment(R.layout.record_modify) {

    private var _binding: RecordModifyBinding? = null
    private val binding get() = _binding!!


    val userId = UserSession.userId ?: 1
    // 사용자 ID
    //아직 카카오로그인이 안된 상태라 노션에 적혀진 1로 하긴 했는데 나중에 카카오로그인 후에 수정필요

    private lateinit var trip: TripResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            trip = it.getParcelable("trip") ?: error("Trip data missing")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = RecordModifyBinding.bind(view)

        // 기존 trip 정보 바인딩
        binding.editTitle.setText(trip.tripName)
        binding.editMemo.setText(trip.description)
        binding.editDate.text = "${trip.startDate} ~ ${trip.endDate}"


        // 저장 버튼 클릭 시
        binding.saveButtonModify.setOnClickListener {
            val updatedTitle = binding.editTitle.text.toString()
            val updatedMemo = binding.editMemo.text.toString()

            // TODO: 수정 API 호출하거나 변경 데이터 전달 처리
        }
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        //editTitle 누르면 제목 수정 다이얼로그 띄우기
        binding.editTitle.setOnClickListener {
            onTitleClick(0)  // 포지션이 필요 없으면 그냥 0
        }

        // 메모 클릭 시 다이얼로그
        binding.editMemo.setOnClickListener {
            onMemoClick(0)
        }

        binding.imageBox.setOnClickListener {
            onImageClick(0)  // position은 필요 없다면 그냥 0
        }

        //gnb숨기기
        requireActivity().findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

    }

    // 각 필드 수정 버튼 클릭 처리

    fun onTitleClick(position: Int) {
        val dialog = Record_Modify_1(
            currentTitle = trip.tripName,
            onTitleUpdated = { newTitle ->
                Log.d("Edit", "새 제목: $newTitle")
                binding.editTitle.setText(newTitle)  // 실제로 UI 업데이트
                // TODO: API 호출 등 처리
            }
        )
        dialog.show(parentFragmentManager, "editTitle")
    }


    fun onDateClick(position: Int) {
        Log.d("Edit", "날짜 클릭됨 at $position")
        // TODO: 날짜 수정 다이얼로그 띄우기 등
    }


    fun onMemoClick(position: Int) {
        val dialog = Record_Modify_2(
            currentTitle = trip.description,
            onTitleUpdated = { newMemo ->
                Log.d("Edit", "새 메모: $newMemo")
                binding.editMemo.setText(newMemo) // UI에 반영
                // TODO: API 호출 등 처리
            }
        )
        dialog.show(parentFragmentManager, "editMemo")
    }


    fun onImageClick(position: Int) {
        // 사진 선택 다이얼로그 띄우기
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogSelectPhotoBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.exitBtn.setOnClickListener {
            dialog.dismiss()
        }

        // tripId는 trip 객체에서 가져오면 됨
        val tripId = trip.id  // 또는 trip.tripId

        // 여행 대표 이미지 리스트 가져오기
        ApiProvider.api.getTripRepresentativeImages(tripId)
            .enqueue(object : Callback<List<TripImageResponse>> {
                override fun onResponse(
                    call: Call<List<TripImageResponse>>,
                    response: Response<List<TripImageResponse>>
                ) {
                    val imageList = response.body() ?: emptyList()
                    val photoAdapter = DialogPhotoSelectAdapter(requireContext(), imageList) { selectedItem ->
                        Log.d("Image", "선택된 이미지: ${selectedItem.imageUrl}")
                        // TODO: 선택된 이미지 처리
                    }
                    bindingDialog.gridView.adapter = photoAdapter
                }

                override fun onFailure(call: Call<List<TripImageResponse>>, t: Throwable) {
                    Log.e("API", "이미지 불러오기 실패: ${t.message}")
                }
            })

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(trip: TripResponse): Record_Modify_Fragment {
            val fragment = Record_Modify_Fragment()
            val bundle = Bundle().apply {
                putParcelable("trip", trip)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
