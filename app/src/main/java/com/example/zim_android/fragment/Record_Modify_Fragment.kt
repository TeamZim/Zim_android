package com.example.zim_android.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zim_android.Adapter.DialogPhotoSelectAdapter
import com.example.zim_android.R
import com.example.zim_android.data.model.SetTripRepresentativeImageRequest
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.data.model.TripResponse
import com.example.zim_android.data.model.TripUpdateRequest
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.databinding.DialogSelectPhotoBinding
import com.example.zim_android.databinding.RecordModifyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DialogEditDateBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward


class Record_Modify_Fragment : Fragment(R.layout.record_modify) {

    private var _binding: RecordModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var updatedTitle: String
    private lateinit var updatedMemo: String


    var selectedItem: TripImageResponse? = null
    lateinit var photoAdapter: DialogPhotoSelectAdapter

    lateinit var startDate: String
    lateinit var endDate: String

    lateinit var tempStartDate: String
    lateinit var tempEndDate: String


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



        // tripId는 trip 객체에서 가져오면 됨
        val tripId = trip.id  // 또는 trip.tripId



        // 기존 trip 정보 바인딩
        binding.editTitle.setText(trip.tripName)
        binding.editMemo.setText(trip.description)
        binding.editDate.text = "${trip.startDate} ~ ${trip.endDate}"
        Glide.with(this).load(trip.representativeImageUrl).centerCrop().into(binding.imageBox)


        startDate = trip.startDate
        endDate = trip.endDate

        tempStartDate = trip.startDate
        tempEndDate = trip.endDate

        updatedTitle = trip.tripName
        updatedMemo = trip.description



        // 저장 버튼 클릭 시
        binding.saveButtonModify.setOnClickListener {

            updatedTitle = binding.editTitle.text.toString()
            updatedMemo = binding.editMemo.text.toString()


            val newTitle = updatedTitle  // 수정된 제목

            // 여행 정보 PUT 요청 보내는 함수
            fun updateTripInfo(imageUrl: String) {
                val updateRequest = TripUpdateRequest(
                    tripName = newTitle,
                    description = updatedMemo,
                    themeId = trip.themeId,
                    representativeImageUrl = imageUrl,
                    startDate = trip.startDate,
                    endDate = trip.endDate
                )

                Log.d("PUT_BODY", """
        여행 수정 요청:
        📝 제목: ${updateRequest.tripName}
        🧾 메모: ${updateRequest.description}
        🎨 테마 ID: ${updateRequest.themeId}
        🖼 대표 이미지: ${updateRequest.representativeImageUrl}
        📅 날짜: ${updateRequest.startDate} ~ ${updateRequest.endDate}
    """.trimIndent())

                api.updateTrip(trip.id, updateRequest)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("PUT_RESULT", "✅ 여행 수정 성공 (code=${response.code()})")
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                            } else {
                                Log.e("PUT_RESULT", "❌ 여행 수정 실패 (code=${response.code()})")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("PUT_RESULT", "💥 여행 수정 API 호출 실패: ${t.message}")
                        }
                    })
            }

            val diaryId = selectedItem?.diaryId
            val selectedImageUrl = selectedItem?.imageUrl ?: trip.representativeImageUrl

            // ✅ 대표 이미지가 선택된 경우 → 먼저 대표 이미지 API 호출, 그 후 updateTripInfo 호출
            if (diaryId != null) {
                val request = SetTripRepresentativeImageRequest(diaryId)
                api.setTripRepresentativeImage(trip.id, request)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("Save", "대표 이미지 설정 완료")
                                if (selectedImageUrl != null) {
                                    updateTripInfo(selectedImageUrl)
                                }
                            } else {
                                Log.e("Save", "대표 이미지 설정 실패: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Save", "대표 이미지 API 호출 실패: ${t.message}")
                        }
                    })
            } else {
                // ✅ 이미지 변경 없이 바로 여행 정보 PUT
                val selectedImageUrl: String = selectedItem?.imageUrl ?: trip.representativeImageUrl ?: ""
                updateTripInfo(selectedImageUrl)


            }


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

        // 이미지 클릭 시 다이얼로그
        binding.imageBox.setOnClickListener {
            onImageClick(0)  // position은 필요 없다면 그냥 0
        }

        // 날짜 클릭 시 다이얼로그
        binding.editDate.setOnClickListener {
            onDateClick(0)
        }


        //gnb숨기기
        requireActivity().findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

    }

    // 각 필드 수정 버튼 클릭 처리
    fun onTitleClick(position: Int) {
        val dialog = Record_Modify_1(
            currentTitle = trip.tripName,
            onTitleUpdated = { newTitle ->
                updatedTitle = newTitle
                binding.editTitle.setText(newTitle)
            }

        )
        dialog.show(parentFragmentManager, "editTitle")
    }

    fun onDateClick(position: Int) {
        Log.d("Edit", "날짜 클릭됨 at $position")
        val dialog = Dialog(requireContext())
        val bindingDialogDate = DialogEditDateBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialogDate.root)

        // 기존 날짜
        bindingDialogDate.startDateText.text = tempStartDate
        bindingDialogDate.endDateText.text = tempEndDate
        dialog.show()

        bindingDialogDate.exitBtn.setOnClickListener {
            dialog.dismiss()
        }

        bindingDialogDate.startDateText.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val endDateMillis = formatter.parse(tempEndDate)?.time ?: MaterialDatePicker.todayInUtcMilliseconds()

            // 종료일 이전만 선택 가능하게 제한
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.before(endDateMillis + 86_400_000L))

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("여행 시작일 선택")
                .setSelection(endDateMillis)
                .setCalendarConstraints(constraintsBuilder.build())
                // .setTheme(R.style.CustomDatePickerTheme)
                .build()

            datePicker.show(parentFragmentManager, "start_date_picker")

            datePicker.addOnPositiveButtonClickListener { selection ->
                // 선택된 날짜는 UTC 기준 long 타입 (milliseconds)
                val selectedDate = Date(selection)

                // 포맷 정의
                val formatter1 = SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN)
                val formatter2 = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

                val formattedDate = formatter2.format(selectedDate)
                tempStartDate = formatter2.format(selectedDate) // 시작 날짜 데이터 임시 저장

                bindingDialogDate.startDateText.text = formattedDate

                binding.editDate.text = "${tempStartDate} ~ ${tempEndDate}"

                if (startDate != tempStartDate || endDate != tempEndDate) {
                    bindingDialogDate.saveBtn.setImageResource(R.drawable.save_btn_active)
                    bindingDialogDate.saveBtn.isClickable = true

                    bindingDialogDate.saveBtn.setOnClickListener {
                        startDate = tempStartDate
                        endDate = tempEndDate
                        dialog.dismiss()
                    }

                } else {
                    bindingDialogDate.saveBtn.setImageResource(R.drawable.save_btn_unactive)
                    bindingDialogDate.saveBtn.isClickable = false
            }
        }
    }

        bindingDialogDate.endDateText.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            Log.d("tempStartDate", tempStartDate)
            val startDateMillis = formatter.parse(tempStartDate)?.time ?: formatter.parse(startDate)?.time ?: MaterialDatePicker.todayInUtcMilliseconds()

            // 시작일 이후만 선택 가능하도록 제한
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(startDateMillis))

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("여행 시작일 선택")
                .setSelection(startDateMillis)
                .setCalendarConstraints(constraintsBuilder.build())
                // .setTheme(R.style.CustomDatePickerTheme)
                .build()

            datePicker.show(parentFragmentManager, "start_date_picker")


            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = Date(selection)

                val formatter1 = SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN)
                val formatter2 = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

                val formattedDate = formatter2.format(selectedDate)
                tempEndDate = formatter2.format(selectedDate) // 종료 날짜 데이터 임시 저장

                bindingDialogDate.endDateText.text = formattedDate
                binding.editDate.text = "${tempStartDate} ~ ${tempEndDate}"

                if (startDate != tempStartDate || endDate != tempEndDate) {
                    bindingDialogDate.saveBtn.setImageResource(R.drawable.save_btn_active)
                    bindingDialogDate.saveBtn.isClickable = true

                    bindingDialogDate.saveBtn.setOnClickListener {
                        startDate = tempStartDate
                        endDate = tempEndDate
                        dialog.dismiss()
                    }
                } else {
                    bindingDialogDate.saveBtn.setImageResource(R.drawable.save_btn_unactive)
                    bindingDialogDate.saveBtn.isClickable = false
                }

                Log.d("CHECK_DATE", "start=$startDate, end=$endDate")


            }
        }

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
        // tripId는 trip 객체에서 가져오면 됨
        val tripId = trip.id  // 또는 trip.tripId

        // 사진 선택 다이얼로그 띄우기
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogSelectPhotoBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.exitBtn.setOnClickListener {
            dialog.dismiss()
        }





        // 여행 대표 이미지 리스트 가져오기
        api.getTripRepresentativeImages(tripId)
            .enqueue(object : Callback<List<TripImageResponse>> {
                override fun onResponse(
                    call: Call<List<TripImageResponse>>,
                    response: Response<List<TripImageResponse>>
                ) {
                    val imageList = response.body() ?: emptyList()

                    // 🔧 5개 미만일 경우 gradient 숨기기
                    bindingDialog.gradientImg.visibility =
                        if (imageList.size < 5) View.GONE else View.VISIBLE

                    photoAdapter = DialogPhotoSelectAdapter(requireContext(), imageList) { clickedItem ->
                        // 같은 항목 다시 클릭하면 선택 해제
                        if (selectedItem == clickedItem) {
                            selectedItem = null
                            bindingDialog.saveBtn.apply {
                                setImageResource(R.drawable.save_btn_unactive)
                                isClickable = false
                            }
                        } else {
                            selectedItem = clickedItem
                            bindingDialog.saveBtn.apply {
                                setImageResource(R.drawable.save_btn_active)
                                isClickable = true
                            }
                        }

                        // 선택 항목 바뀌었으니 갱신
                        photoAdapter.setSelectedItem(selectedItem)
                    }

                    bindingDialog.gridView.adapter = photoAdapter

                    // 저장 버튼 초기 상태 비활성화
                    bindingDialog.saveBtn.apply {
                        setImageResource(R.drawable.save_btn_unactive)
                        isClickable = false
                    }

                    // 저장 버튼 클릭 시 처리
                    bindingDialog.saveBtn.setOnClickListener {
                        selectedItem?.let {
                            Glide.with(this@Record_Modify_Fragment).load(it.imageUrl).centerCrop().into(binding.imageBox)
                            dialog.dismiss()
                        }
                    }
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

