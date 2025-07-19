package com.example.zim_android.fragment

import CountryDropdownAdapter
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.example.zim_android.Adapter.DialogEmotionSelectAdapter
import com.example.zim_android.R
import com.example.zim_android.data.model.AddVisitedCountryRequest
import com.example.zim_android.data.model.CountrySearchResponse
import com.example.zim_android.data.model.Emotion
import com.example.zim_android.data.model.VisitedCountryResponse
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.databinding.DialogSelectEmotionColorBinding
import com.example.zim_android.databinding.DropdownLayoutBinding
import com.example.zim_android.databinding.ViewMapDialog1Binding
import com.example.zim_android.databinding.ViewMapFragmentBinding
import com.example.zim_android.util.PreferenceUtil
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMapFragment : Fragment(R.layout.view_map_fragment) {

    private val visitedCountriesDynamic = mutableListOf<VisitedCountryResponse>()

    val userId = 1 // 임시로 테스트할 userId
//    var suppressDropdown = false

    // 나라별로 저장된 색상
    private val countryColorMap = mutableMapOf<String, String>()

    private var _binding: ViewMapFragmentBinding? = null
    private val binding get() = _binding!!

    // 필요한 정보(국가, 감정색 코드, 아이디) 저장하는 변수들
    // 지도에 추가할
    var selectedCountryCode: String = ""
    var selectedEmotionColorCode: String = "#EEEEEE"
    var selectedEmotionId: Int = 1

    private lateinit var dropdownAdapter: CountryDropdownAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //반드시 여기서 먼저 초기화해야 binding 사용 가능
        _binding = ViewMapFragmentBinding.inflate(inflater, container, false)

        binding.mapWebView.settings.javaScriptEnabled = true
        binding.mapWebView.loadUrl("file:///android_asset/world.svg")

        //웹뷰 설정
        binding.mapWebView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            builtInZoomControls = true // 확대/축소 필요시
            displayZoomControls = false

            binding.mapWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    colorVisitedCountriesOnMap()

                }

            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addRecordBtn.setOnClickListener {
            showAddRecordDialog1()
        }

        fetchVisitedCountries()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun updateCountryColor(countryCode: String, colorCode: String) {
        countryColorMap[countryCode] = colorCode
        val js = "document.getElementById('$countryCode').style.fill = '$colorCode';"
        binding.mapWebView.evaluateJavascript(js, null)  // 여기 binding으로 변경
    }


    private fun fetchVisitedCountries() {
        val userId = 1
        Log.d("API 호출", "userId = $userId")
        api.getVisitedCountries(userId).enqueue(object : Callback<VisitedCountryListResponse> {
            override fun onResponse(
                call: Call<VisitedCountryListResponse>,
                response: Response<VisitedCountryListResponse>
            ) {
                Log.d("API 호출", "onResponse 호출됨")

                val body = response.body()
                if (body == null) {
                    Log.e("API 응답", "body가 null임!") // ✅ 바로 이거 찍어봐
                } else {
                    val result = body.data
                    Log.d("API 응답", "받은 국가 수 = ${result.size}")
                    visitedCountriesDynamic.clear()
                    visitedCountriesDynamic.addAll(result)
                    colorVisitedCountriesOnMap()
                }
            }


            override fun onFailure(call: Call<VisitedCountryListResponse>, t: Throwable) {
                Log.e("방문 국가 가져오기 실패", t.message.toString())
            }
        })

    }

    //실제 색칠하는 함수 -> 연동하면서 수정했음
    private fun colorVisitedCountriesOnMap() {
        for (country in visitedCountriesDynamic) {
            Log.d("지도색칠", "국가=${country.countryCode}, 색=${country.color}")

            val js = """
                    var el = document.getElementById('${country.countryCode}');
                    if (el) {
                        el.style.fill = '${country.color}';
                    }
                """.trimIndent()

            binding.mapWebView.evaluateJavascript(js, null)
        }
    }

    //wrapper 만들기
    data class VisitedCountryListResponse(
        val data: List<VisitedCountryResponse>
    )


    // 과거 여행 추가 다이얼로그 띄우기
    private fun showAddRecordDialog1() {
        val dialog1 = Dialog(requireContext())
        val dialog1Binding = ViewMapDialog1Binding.inflate(layoutInflater)
        dialog1.setContentView(dialog1Binding.root)

//        val inputField = dialog1Binding.dialog1CountryListTextInput

        // x버튼 클릭시 다이얼로그 내리기
        dialog1Binding.dialog1ExitBtn.setOnClickListener {
            dialog1.dismiss()
        }

        // 드롭다운 세팅
//        Dropdown(dialog1Binding)
        setupCountryDropdown(dialog1Binding)
//
////         사용자가 드롭다운에서 나라를 선택한 경우
//        dialog1Binding.countryEditText.setOnItemClickListener { parent, _, position, _ ->
//            val selectedCountry = parent.getItemAtPosition(position) as CountrySearchResponse
//            val countryCode = selectedCountry.countryCode
//            selectedCountryCode = countryCode
//
//            // 포커스 제거 (깜빡이는거 제거)
//            dialog1Binding.dialog1CountryListTextInput.clearFocus()
//
////            suppressDropdown = true // 드롭다운 다시 열리지 않게 설정
//
//            inputField.clearFocus()
//            inputField.dismissDropDown()
//
//            // 키보드 내리기
//            // getSystemService = 입력 서비스 가지고 오기, InputMethodManager 타입으로 다운캐스팅
//            val imm =
//                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(
//                dialog1Binding.dialog1CountryListTextInput.windowToken,
//                0
//            ) // 키보드 내려
//
//
//
//            dialog1Binding.dialog1SaveBtn.setImageResource(R.drawable.save_btn_active)
//            dialog1Binding.dialog1SaveBtn.isClickable = true // 국가 선택시 저장 btn active
//        }

//        textInput.setOnClickListener {
//            // 다이얼로그 안에서는 포커스가 textinput이 아니라 다이얼로그로 가서 키보드가 자동으로 안 올라올 수 있음.
//            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(dialog1Binding.dialog1CountryListTextInput, InputMethodManager.SHOW_IMPLICIT)
//        }


        // 저장 버튼 클릭 시 (dialog1Binding 기준)
        // !!! -------    류지야 여기에서 selectedCountryCode랑 selectedEmotionId 이용해서 수정해뒀는데 검토 부탁해   ------- !!!
        dialog1Binding.dialog1SaveBtn.setOnClickListener {
            if (selectedCountryCode.isNotEmpty() && selectedEmotionId != null) {
                // 지도에 색상 추가
                updateCountryColor(selectedCountryCode, selectedEmotionColorCode)

                // 백엔드에 전달할 객체 body AddVisitedCountryRequest 및 userId 받아오기
                val request = AddVisitedCountryRequest(
                    countryCode = selectedCountryCode,
                    emotionId = selectedEmotionId
                )
                val userId = PreferenceUtil.getUserId(requireContext())

                // 백엔드에 저장
                api.addVisitedCountry(userId, request).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("저장 성공", "방문 국가 저장 완료")
                        } else {
                            Log.e("저장 실패", "응답 코드: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("저장 실패", t.message.toString())
                    }
                })

                dialog1.dismiss()
                dialog1Binding.dialog1SaveBtn.isClickable = false
            }

            //

        }


        // 감정색 다이얼로그 띄우기
        showAddRecordDialog2(dialog1Binding)

        dialog1.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog1.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog1.show()
    }


    // 감정색 선택 다이얼로그 띄우는 부분
    private fun showAddRecordDialog2(dialog1Binding: ViewMapDialog1Binding) {
        // 감정색 다이얼로그 작업
        dialog1Binding.selectColorBtn.setOnClickListener {
            // 두 번째 다이얼로그 가지고 오기
            val dialog2 = Dialog(requireContext())
            val dialog2Binding = DialogSelectEmotionColorBinding.inflate(layoutInflater)
            dialog2.setContentView(dialog2Binding.root)

            // < 버튼 클릭시 다이얼로그 내리기
            dialog2Binding.backToDialog1.setOnClickListener {
                dialog2.dismiss()
            }

            api.getEmotions().enqueue(object : Callback<List<Emotion>> {
                override fun onResponse(
                    call: Call<List<Emotion>>,
                    response: Response<List<Emotion>>
                ) {
                    if (response.isSuccessful) {
                        val emotionList = response.body() ?: emptyList()
                        val slicedEmotionList = if (emotionList.size > 1) {
                            emotionList.subList(1, minOf(13, emotionList.size)) // index 1 ~ 12
                        } else {
                            emptyList()
                        }

                        // 어댑터에 연결
                        val emotionSelectAdapter = DialogEmotionSelectAdapter(
                            context = requireContext(),
                            items = slicedEmotionList,
                            onItemSelected = { selectedEmotion ->
                                // 감정 선택 시 처리 부분
                                // 저장 버튼 활성화 및 클릭 가능하게하기
                                dialog2Binding.dialog2SaveBtn.setImageResource(R.drawable.save_btn_active)
                                dialog2Binding.dialog2SaveBtn.isClickable = true
                            }
                        )
                        // RecyclerView 혹은 GridView에 setAdapter(adapter) 등으로 연결
                        dialog2Binding.dialog2SaveBtn.setOnClickListener {
                            val selectedItem = emotionSelectAdapter.getSelectedItem()
                            if (selectedItem != null) {
                                dialog1Binding.dialog1SelectedColorImg.setImageResource(R.drawable.emotion_color_base_circle)
                                dialog1Binding.dialog1SelectedColorImg.setColorFilter(
                                    Color.parseColor(
                                        selectedItem.colorCode
                                    )
                                )
                                dialog1Binding.dialog1SelectedColorText.text = selectedItem.name

                                selectedEmotionColorCode = selectedItem.colorCode // 컬러 코드 저장
                                selectedEmotionId = selectedItem.id // 컬러 아이디 저장
                                dialog2.dismiss()
                                dialog2Binding.dialog2SaveBtn.isClickable = false
                            }
                        }

                        // 그리드 레이아웃에 아이템 추가
                        for (i in 0 until emotionSelectAdapter.getCount()) {
                            val view = emotionSelectAdapter.getView(i)
                            dialog2Binding.colorListGrid.addView(view)
                        }

                        emotionSelectAdapter.setOnGridUpdateCallback {
                            dialog2Binding.colorListGrid.removeAllViews()
                            for (i in 0 until emotionSelectAdapter.getCount()) {
                                val view = emotionSelectAdapter.getView(i)
                                dialog2Binding.colorListGrid.addView(view)
                            }
                        } // 초기화 한 번 호출

                    }
                }

                override fun onFailure(call: Call<List<Emotion>>, t: Throwable) {
                    Log.e("감정 불러오기 실패", t.message.toString())
                }
            })

            dialog2.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog2.show()

        }

    }

    private fun setupCountryDropdown(dialog1Binding: ViewMapDialog1Binding) {
        val editText = dialog1Binding.countryEditText
        var userManuallyEdited = false

        // RecyclerView를 팝업 내용으로 사용
        val dropdownBinding = DropdownLayoutBinding.inflate(layoutInflater, null, false)
        val recyclerView = dropdownBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val popupWindow = PopupWindow(
            dropdownBinding.root, // 여기 중요
            editText.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (userManuallyEdited) {
                    selectedCountryCode = "" // ✅ 기존 선택값 초기화
                    dialog1Binding.dialog1SaveBtn.setImageResource(R.drawable.save_btn_unactive)
                    dialog1Binding.dialog1SaveBtn.isClickable = false
                }

                val keyword = s.toString()
                Log.d("keyword", keyword)

                api.searchCountry(keyword).enqueue(object : Callback<CountrySearchListResponse> {
                    override fun onResponse(
                        call: Call<CountrySearchListResponse>,
                        response: Response<CountrySearchListResponse>
                    ) {
                        val result = response.body()?.data ?: return
                        dropdownAdapter.updateItems(result)

                        if (!popupWindow.isShowing) {
                            popupWindow.width = editText.width
                            popupWindow.showAsDropDown(editText, 0, -18)
                        }
                    }

                    override fun onFailure(call: Call<CountrySearchListResponse>, t: Throwable) {
                        Log.e("API 실패", t.message ?: "Unknown error")
                    }
                })

            }

        }

        editText.setOnTouchListener { _, _ ->
            // 사용자가 손으로 눌렀을 경우 → 이후 텍스트가 바뀌면 수정으로 간주
            userManuallyEdited = true
            false
        }


        editText.addTextChangedListener(textWatcher)

        dropdownAdapter = CountryDropdownAdapter { selectedItem ->

            editText.removeTextChangedListener(textWatcher)

            editText.setText(selectedItem.countryName)
            selectedCountryCode = selectedItem.countryCode
            userManuallyEdited = false // 선택된 항목이니까 "수정 아님"

            dialog1Binding.dialog1SaveBtn.setImageResource(R.drawable.save_btn_active)
            dialog1Binding.dialog1SaveBtn.isClickable = true

            popupWindow.dismiss()

            editText.post {
                editText.addTextChangedListener(textWatcher)
            }
        }
        recyclerView.adapter = dropdownAdapter
    }


    data class CountrySearchListResponse(
        val data: List<CountrySearchResponse>
    )
}




