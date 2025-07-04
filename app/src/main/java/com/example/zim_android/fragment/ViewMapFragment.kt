package com.example.zim_android.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.zim_android.Adapter.DialogViewMap2Adapter
import com.example.zim_android.R
import com.example.zim_android.data.model.EmotionColorData
import com.example.zim_android.databinding.ViewMapDialog1Binding
import com.example.zim_android.databinding.ViewMapDialog2Binding
import com.example.zim_android.databinding.ViewMapFragmentBinding

class ViewMapFragment : Fragment(R.layout.view_map_fragment) {

    // 예시 데이터: countryCode -> colorCode
    val visitedCountries = mapOf(
        "Republic of Korea" to "#FF6B6B",  // 한국
        "Japan" to "#FF2B2B",  // 일본
        "France" to "#FFD93D", // 프랑스
        "China" to "#FFFF00"
    )


    private lateinit var webView: WebView

    // 나라별로 저장된 색상
    private val countryColorMap = mutableMapOf<String, String>()

    private var _binding: ViewMapFragmentBinding? = null
    private val binding get() = _binding!!

    // 필요한 정보(국가, 감정색) 저장하는 변수들
    var selectedCountry: String = ""
    var selectedEmotionColorCode: String = "" // 선택을 안 한 경우 "" 상태임

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

                    // 페이지 로드 끝나면 JS로 색칠
                    for ((countryCode, colorCode) in visitedCountries) {
                        val js = """
    var elements = document.getElementsByClassName('$countryCode');
    for (var i = 0; i < elements.length; i++) {
        elements[i].style.fill = '$colorCode';
    }
""".trimIndent()
                        binding.mapWebView.evaluateJavascript(js, null)

                    }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun updateCountryColor(countryCode: String, colorCode: String) {
        countryColorMap[countryCode] = colorCode
        val js = "document.getElementById('$countryCode').style.fill = '$colorCode';"
        binding.mapWebView.evaluateJavascript(js, null)  // ✅ 여기 binding으로 변경
    }


    //
    private fun showAddRecordDialog1() {
        val dialog1 = Dialog(requireContext())
        val dialog1Binding = ViewMapDialog1Binding.inflate(layoutInflater)
        dialog1.setContentView(dialog1Binding.root)

        // x버튼 클릭시 다이얼로그 내리기
        dialog1Binding.dialog1ExitBtn.setOnClickListener {
            dialog1.dismiss()
        }

        // 임시 더미 리스트
        val countryNames = listOf("Korea", "한국", "일본", "미국", "프랑스", "독일", "중국", "영국", "이탈리아", "스페인", "러시아", "브라질", "캐나다", "멕시코", "사우디아라비아", "태국", "인도", "베트남", "싱가포르", "남아프리카공화국", "스웨덴", "호주", "네덜란드", "뉴질랜드", "노르웨이", "핀란드", "스위스", "포르투갈", "폴란드", "덴마크", "아르헨티나", "칠레", "이집트", "터키", "아랍에미리트", "인도네시아")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames)
        dialog1Binding.dialog1CountryListTextInput.setAdapter(adapter)

        dialog1Binding.dialog1CountryListTextInput.setOnClickListener {
            // 다이얼로그 안에서는 포커스가 textinput이 아니라 다이얼로그로 가서 키보드가 자동으로 안 올라올 수 있음.
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(dialog1Binding.dialog1CountryListTextInput, InputMethodManager.SHOW_IMPLICIT)
        }

        // 사용자가 드롭다운에서 나라를 선택한 경우
        dialog1Binding.dialog1CountryListTextInput.setOnItemClickListener { parent, _, position, _ -> // _는 view, id인데 사용 안해서 생략함.
            selectedCountry = parent.getItemAtPosition(position).toString() // 사용자가 선택한 국가명 텍스트로 저장
            // 포커스 제거 (깜빡이는거 제거)
            dialog1Binding.dialog1CountryListTextInput.clearFocus()

            // 키보드 내리기
            // getSystemService = 입력 서비스 가지고 오기
            //InputMethodManager 타입으로 다운캐스팅
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialog1Binding.dialog1CountryListTextInput.windowToken, 0) // 키보드 내려

            dialog1Binding.dialog1SaveBtn.setImageResource(R.drawable.save_btn_active)
            dialog1Binding.dialog1SaveBtn.isClickable = true
        }

        // 저장 버튼 클릭 시 (dialog1Binding 기준)
        dialog1Binding.dialog1SaveBtn.setOnClickListener {
            if (selectedCountry.isNotEmpty() && selectedEmotionColorCode.isNotEmpty()) {
                val countryCode = countryNameToCode(selectedCountry)
                if (countryCode != null) {
                    updateCountryColor(countryCode, selectedEmotionColorCode)
                }
                dialog1.dismiss()
                dialog1Binding.dialog1SaveBtn.isClickable = false
                selectedCountry = ""
                selectedEmotionColorCode = ""
            }
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

    private fun showAddRecordDialog2(dialog1Binding: ViewMapDialog1Binding) {
        // 감정색 다이얼로그 작업
        dialog1Binding.selectColorBtn.setOnClickListener {
            // 두 번째 다이얼로그 가지고 오기
            val dialog2 = Dialog(requireContext())
            val dialog2Binding = ViewMapDialog2Binding.inflate(layoutInflater)
            dialog2.setContentView(dialog2Binding.root)

            // < 버튼 클릭시 다이얼로그 내리기
            dialog2Binding.backToDialog1.setOnClickListener {
                dialog2.dismiss()
            }


            // 어댑터 연결
            // 사용자가 감정색을 선택한 경우
            val emotionDialogAdapter = DialogViewMap2Adapter(requireContext(), EmotionColorData.emotionColorList){ // 이 다음부터가 람다 인자를 보내는 부분
                    selectedItem ->
                // 저장 버튼 활성화 및 클릭 가능하게하기
                dialog2Binding.dialog2SaveBtn.setImageResource(R.drawable.save_btn_active)
                dialog2Binding.dialog2SaveBtn.isClickable = true
            }

            // 저장 버튼 클릭 시
            dialog2Binding.dialog2SaveBtn.setOnClickListener {
                // 감정 이미지와 텍스트 설정
                val selectedItem = emotionDialogAdapter.getSelectedItem()
                if (selectedItem != null) {
                    dialog1Binding.dialog1SelectedColorImg.setImageResource(selectedItem.imageResId)
                    dialog1Binding.dialog1SelectedColorText.text = selectedItem.name
                    // 감정색명 저장
                    selectedEmotionColorCode = selectedItem.colorCode
                    dialog2.dismiss()
                    dialog2Binding.dialog2SaveBtn.isClickable = false
                }
            }

            for (i in 0 until emotionDialogAdapter.getCount()) {
                val view = emotionDialogAdapter.getView(i)
                dialog2Binding.colorListGrid.addView(view) // 그리드 레이아웃에 아이템 추가
            }

            emotionDialogAdapter.setOnGridUpdateCallback{
                dialog2Binding.colorListGrid.removeAllViews()
                for (i in 0 until emotionDialogAdapter.getCount()) {
                    val view = emotionDialogAdapter.getView(i)
                    dialog2Binding.colorListGrid.addView(view)
                }
            } // 초기화 한 번 호출
            // 함수 안 내용은 호출할 때 정의하는 것


            dialog2.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialog2.show()



            }


        }

    }
// 🟨 countryNameToCode 함수: 반드시 클래스 마지막에 붙여줘야 함 (다른 함수 밖, 클래스 안)
private fun countryNameToCode(name: String): String? {
    return mapOf(
        "한국" to "KR",
        "일본" to "Japan",
        "미국" to "US",
        "프랑스" to "FR",
        "독일" to "DE",
        "중국" to "CN",
        "영국" to "GB",
        "이탈리아" to "IT",
        "스페인" to "ES",
        "러시아" to "RU",
        "브라질" to "BR",
        "캐나다" to "CA",
        "멕시코" to "MX",
        "사우디아라비아" to "SA",
        "태국" to "TH",
        "인도" to "IN",
        "베트남" to "VN",
        "싱가포르" to "SG",
        "남아프리카공화국" to "ZA",
        "스웨덴" to "SE",
        "호주" to "AU",
        "네덜란드" to "NL",
        "뉴질랜드" to "NZ",
        "노르웨이" to "NO",
        "핀란드" to "FI",
        "스위스" to "CH",
        "포르투갈" to "PT",
        "폴란드" to "PL",
        "덴마크" to "DK",
        "아르헨티나" to "AR",
        "칠레" to "CL",
        "이집트" to "EG",
        "터키" to "TR",
        "아랍에미리트" to "AE",
        "인도네시아" to "ID",
        "Korea" to "KR"
    )[name]
}




