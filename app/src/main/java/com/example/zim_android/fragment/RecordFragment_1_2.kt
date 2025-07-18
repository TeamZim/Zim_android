package com.example.zim_android.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.Adapter.ThemeSelectGridViewAdapter
import com.example.zim_android.R
import com.example.zim_android.Record_2_1_Activity
import com.example.zim_android.data.model.Theme
import com.example.zim_android.data.model.TripCreateRequest
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.DiaryTempStore
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.Record12FragmentBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class RecordFragment_1_2: Fragment(R.layout.record_1_2_fragment){

    private var _binding: Record12FragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var themeAdapter: ThemeSelectGridViewAdapter
    private lateinit var selectedTheme: Theme

    val currentTripId = 1 // 임시로 1로 지정


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Record12FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 입력 감지 함수
        setupTextWatchers()
        binding.commonHeader.tvTitle.text = "기록하기"

        // root에 포커스 해제 설정
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clearFocusAndHideKeyboard()
            }
            false
        }

        // 나중에 추가하기
        // clearFocusAndHideKeyboard()

        // 이전 화면으로 돌아가기
        binding.backBtn.setOnClickListener {
            DiaryTempStore.clear() // 저장되어있던 임시 일기 정보들 초기화
            findNavController().navigate(R.id.action_recordFragment_1_2_to_recordFragment_1_1)
        }

        api.getThemelist().enqueue(object : Callback<List<Theme>> {
            override fun onResponse(call: Call<List<Theme>>, response: Response<List<Theme>>) {
                if (response.isSuccessful) {
                    val themeList = response.body() ?: emptyList()

                    if (themeList.isNotEmpty()) {
                        selectedTheme = themeList[0] // 기본값 설정

                        themeAdapter = ThemeSelectGridViewAdapter(requireContext(), themeList) { clickedItem ->
                            if (selectedTheme != clickedItem) {
                                selectedTheme = clickedItem
                                themeAdapter.setSelectedTheme(clickedItem)
                            }
                        }

                        binding.themeSelect.adapter = themeAdapter
                        themeAdapter.setSelectedTheme(selectedTheme)
                    }
                }
            }

            override fun onFailure(call: Call<List<Theme>>, t: Throwable) {
                Log.e("ThemeLoad", "불러오기 실패: ${t.message}")
            }
        })



        // 다음 버튼 클릭
        binding.saveBtn.setOnClickListener {
            val intent = Intent(requireContext(), Record_2_1_Activity::class.java)
            // UserSession의 currentTripId 업데이트
            UserSession.currentTripId = currentTripId

            // 일기 생성 정보 추가
            DiaryTempStore.tripId = UserSession.currentTripId // UserSession에 저장된 현재 userId 사용

            // 여행(카드) 추가
            // 이 부분의 경우 나중에 일기 생성에서 저장 버튼 클릭시로 옮기기
            val request = TripCreateRequest(
                tripName = binding.tripTitle.text.toString(),
                description = binding.tripDescription.text.toString(),
                themeId = selectedTheme.id,
                userId = UserSession.userId ?: error("User ID is null")
            )
            api.createTrip(request)
            // 여기까지

            Log.d("request.toString()", request.toString())
            // 만약 카메라로 사진 안 찍고 여행시작 버튼만 눌러도 여행 추가라면 여기에 카드 추가 구현해야함.

            startActivity(intent)
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // 포커스 헤재하는 함수
    private fun clearFocusAndHideKeyboard() {
        val focused = requireActivity().currentFocus
        focused?.clearFocus()
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(focused?.windowToken, 0)
    }


    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val titleNotEmpty = binding.tripTitle.text?.isNotBlank() == true
                val descNotEmpty = binding.tripDescription.text?.isNotBlank() == true
                binding.saveBtn.isClickable = titleNotEmpty && descNotEmpty
                if (titleNotEmpty && descNotEmpty) {
                    binding.saveBtn.setImageResource(R.drawable.create_trip_btn_active)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.tripTitle.addTextChangedListener(textWatcher)
        binding.tripDescription.addTextChangedListener(textWatcher)
    }
}
