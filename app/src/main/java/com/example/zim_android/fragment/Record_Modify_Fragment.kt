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

class Record_Modify_Fragment : Fragment(R.layout.record_modify) {

    private var _binding: RecordModifyBinding? = null
    private val binding get() = _binding!!

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

        //gnb숨기기
        requireActivity().findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

    }

    // 각 필드 수정 버튼 클릭 처리

   fun onTitleClick(position: Int) {
        val dialog = Record_Modify_1(
            currentTitle = "현재 제목", // 실제 tripList[position].title 등으로 바꿔도 됨
            onTitleUpdated = { newTitle ->
                Log.d("Edit", "새 제목: $newTitle")
                // TODO: API로 업데이트 등 처리
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
            currentTitle = "현재 메모",
            onTitleUpdated = { newMemo ->
                Log.d("Edit", "새 메모: $newMemo")
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
