package com.example.zim_android.View

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Geocoder
import android.media.ExifInterface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.zim_android.Adapter.DialogEmotionSelectAdapter
import com.example.zim_android.MainActivity
import com.example.zim_android.R
import com.example.zim_android.data.model.AddVisitedCountryRequest
import com.example.zim_android.data.model.DiaryCreateRequest
import com.example.zim_android.data.model.DiaryImageRequest
import com.example.zim_android.data.model.DiaryResponse
import com.example.zim_android.data.model.Emotion
import com.example.zim_android.data.network.ApiProvider
import com.example.zim_android.data.network.ApiProvider.api
import com.example.zim_android.data.network.DiaryTempStore.city
import com.example.zim_android.data.network.DiaryTempStore.countryCode
import com.example.zim_android.data.network.DiaryTempStore.emotionId
import com.example.zim_android.data.network.UserSession
import com.example.zim_android.databinding.DialogSelectEmotionColorBinding
import com.example.zim_android.databinding.DialogSelectWeatherBinding
import com.example.zim_android.databinding.Record4Binding
import com.example.zim_android.fragment.Record_4_1
import com.example.zim_android.util.PreferenceUtil
import com.google.android.gms.location.LocationServices
import com.kakao.sdk.user.model.User
import okhttp3.MultipartBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import java.util.Locale


class Record_4_Activity : AppCompatActivity() {

    private lateinit var binding: Record4Binding

    private var selectedEmotionId: Int = -1
    private var selectedWeatherId: Int? = null

    private var uploadedImageUrl1: String = ""
    private var uploadedImageUrl2: String = ""
    private var uploadedAudioUrl: String = ""
    private var representIndex: Int = 0 // 이미 너가 갖고 있음
    private var tripId: Int = 0 // 전달받아서 저장해두기

    val userId = UserSession.userId // 임시로 테스트할 userId

    //감정색 기본 세팅
    private var selectedEmotionColorCode: String = "#D9D9D9"

    //시간 받아오기 세팅
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val dateTime = LocalDateTime.now().format(formatter)

    val userSession_userId = UserSession.userId ?: 1

    companion object {
        const val REQUEST_DIARY_INPUT = 1001
        const val RESULT_DIARY_TEXT = "diary_text"
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Record4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripName = intent.getStringExtra("tripName")
        binding.tripTitle.text = tripName ?: "제목 없음"



        //녹음 권한 설정
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 200)
        }


        // 헤더 설정
        binding.backBtnHeader.tvTitle.text = "기록하기"
        binding.backBtnHeader.backBtn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // 전달받은 값
        val imagePath1 = intent.getStringExtra("imagePath1")
        val imagePath2 = intent.getStringExtra("imagePath2")
        representIndex = intent.getIntExtra("representIndex", 0)
        tripId = intent.getIntExtra("tripId", 0)


        // 이미지 세팅
        if (!imagePath1.isNullOrEmpty()) {
            val bitmap1 = BitmapFactory.decodeFile(imagePath1)
            binding.image1.setImageBitmap(bitmap1)
        }
        if (!imagePath2.isNullOrEmpty()) {
            val bitmap2 = BitmapFactory.decodeFile(imagePath2)
            binding.image2.setImageBitmap(bitmap2)
        }

        val imageFile = File(
            if (representIndex == 0) imagePath1!! else imagePath2!!
        )



        binding.root.post {
            applyPhotoMetadataToTextViews(imageFile, this)
        }


        //글자 수 세는
        binding.placeInput.addTextChangedListener {
            val count = it?.length ?: 0
            binding.placeCounter.text = "$count/16"
        }


        // 대표 라벨 처리
        binding.labelRepresent1.visibility = if (representIndex == 0) View.VISIBLE else View.GONE
        binding.labelRepresent2.visibility = if (representIndex == 1) View.VISIBLE else View.GONE

        //감정색 선택 다이얼로그
        val emotionButton = findViewById<LinearLayout>(R.id.emotion_button)

        val context = this // 이렇게 미리 정의해두고
        binding.emotionButton.setOnClickListener {
            showEmotionColorDialog(this)
        }

        binding.weatherButton.setOnClickListener {
            showWeatherDialog(this)
        }

        setupRecordingUI()



        binding.diaryInput.setOnClickListener {
            Record_4_1(
                currentText = binding.diaryInput.text.toString(), // 이전 내용
                onTextConfirmed = { updated ->
                    binding.diaryInput.setText(updated) // 결과 반영
                }
            ).show(supportFragmentManager, "RecordTextDialog")
        }

        binding.saveButton.setOnClickListener {
            val detailedLocation = binding.placeInput.text.toString()
            val content = binding.diaryInput.text.toString()

            // 감정이 선택되지 않았다면 1로 기본 설정
            if (selectedEmotionId == -1) {
                selectedEmotionId = 1
            }

            if (imagePath1.isNullOrEmpty() || imagePath2.isNullOrEmpty()) {
                Log.e("❌", "이미지 경로 없음")
                return@setOnClickListener
            }

            uploadFile(imagePath1, "images") { url1 ->
                uploadedImageUrl1 = url1 ?: ""
                uploadFile(imagePath2, "images") { url2 ->
                    uploadedImageUrl2 = url2 ?: ""
                    if (audioFilePath.isNotEmpty()) {
                        uploadFile(audioFilePath, "audio") { audioUrl ->
                            uploadedAudioUrl = audioUrl ?: ""
                            postDiary(city, content, detailedLocation)
                        }
                    } else {
                        postDiary(city, content, detailedLocation)
                    }
                }
            }
        }



    }

    private fun postDiary(city: String, content: String, detailedLocation: String) {

        val frontImage = DiaryImageRequest(
            imageUrl = uploadedImageUrl1,
            cameraType = "FRONT",
            isRepresentative = representIndex == 0
        )
        val backImage = DiaryImageRequest(
            imageUrl = uploadedImageUrl2,
            cameraType = "BACK",
            isRepresentative = representIndex == 1
        )

        val diaryRequest = DiaryCreateRequest(
            userId = userSession_userId,
            tripId = tripId,
            countryCode = "KR",
            city = city,
            dateTime = dateTime,
            content = content,
            images = listOf(frontImage, backImage),
            detailedLocation = detailedLocation,
            audioUrl = uploadedAudioUrl,
            emotionId = selectedEmotionId,
            weatherId = selectedWeatherId,
        )

        Log.d("📝 DiaryRequest 디버깅", diaryRequest.toString())

        ApiProvider.api.createDiary(diaryRequest).enqueue(object : Callback<DiaryResponse> {
            override fun onResponse(call: Call<DiaryResponse>, response: Response<DiaryResponse>) {
                if (response.isSuccessful) {

                    if (userId != null) {
                        val request = AddVisitedCountryRequest(
                            countryCode = countryCode ?: "KR",
                            emotionId = selectedEmotionId,
                        )

                        api.addVisitedCountry(userId, request).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("✅ 국가 저장", "성공")
                                } else {
                                    Log.e("❌ 국가 저장 실패", response.errorBody()?.string().toString())
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("❌ 국가 저장 에러", t.message.toString())
                            }
                        })
                    }

                    val intent = Intent(this@Record_4_Activity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("gotoFragment", "ViewCard")
                    }
                    startActivity(intent)
                    finish()

                } else {
                    Log.e(
                        "일기 저장 실패",
                        "응답 코드: ${response.code()}, 메시지: ${response.errorBody()?.string()}"
                    )
                }
            }




            override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
                Log.e("일기 저장 실패", "에러: ${t.message}")
            }
        })


    }


    private fun showEmotionColorDialog(context: Context) {
        val dialog = Dialog(context)
        val dialogBinding = DialogSelectEmotionColorBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        // 닫기 버튼 클릭 시 다이얼로그 닫기
        dialogBinding.backToDialog1.setOnClickListener {
            dialog.dismiss()
        }

        // 감정 리스트 받아오기
        ApiProvider.api.getEmotions().enqueue(object : Callback<List<Emotion>> {
            override fun onResponse(call: Call<List<Emotion>>, response: Response<List<Emotion>>) {
                if (response.isSuccessful) {
                    val emotionList = response.body() ?: emptyList()
                    val slicedEmotionList = if (emotionList.size > 1) {
                        emotionList.subList(1, minOf(13, emotionList.size)) // index 1 ~ 12
                    } else {
                        emptyList()
                    }

                    val adapter = DialogEmotionSelectAdapter(
                        context = context,
                        items = slicedEmotionList,
                        onItemSelected = {
                            // 선택 시 저장 버튼 활성화
                            dialogBinding.dialog2SaveBtn.setImageResource(R.drawable.save_btn_active)
                            dialogBinding.dialog2SaveBtn.isClickable = true
                        }
                    )

                    dialogBinding.dialog2SaveBtn.setOnClickListener {
                        val selectedItem = adapter.getSelectedItem()
                        if (selectedItem != null) {
                            // 선택된 감정에 따라 UI 변경
                            binding.emotionText.text = selectedItem.name
                            binding.emotionCircle.setImageResource(R.drawable.emotion_color_base_circle)
                            binding.emotionCircle.setColorFilter(Color.parseColor(selectedItem.colorCode))

                            // 전역 변수 저장
                            selectedEmotionId = selectedItem.id
                            selectedEmotionColorCode = selectedItem.colorCode

                            dialog.dismiss()
                            dialogBinding.dialog2SaveBtn.isClickable = false
                        }
                    }

                    // 그리드에 뷰 추가
                    for (i in 0 until adapter.getCount()) {
                        val view = adapter.getView(i)
                        dialogBinding.colorListGrid.addView(view)
                    }

                    // 선택 변경 시 그리드 갱신 콜백
                    adapter.setOnGridUpdateCallback {
                        dialogBinding.colorListGrid.removeAllViews()
                        for (i in 0 until adapter.getCount()) {
                            val view = adapter.getView(i)
                            dialogBinding.colorListGrid.addView(view)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Emotion>>, t: Throwable) {
                Log.e("감정 불러오기 실패", t.message.toString())
            }
        })

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private var selectedWeatherIcon: ImageView? = null

    private fun showWeatherDialog(context: Context) {
        val dialog = Dialog(context)
        val dialogBinding = DialogSelectWeatherBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val weatherMap = mapOf(
            dialogBinding.weatherSunny to Triple("맑음", R.drawable.weather_sunny_sel, 1),
            dialogBinding.weatherCloudy to Triple("흐림", R.drawable.weather_cloud_sel, 2),
            dialogBinding.weatherRainy to Triple("비", R.drawable.weather_rainy_sel, 3),
            dialogBinding.weatherWindy to Triple("바람", R.drawable.weather_windy_sel, 4),
            dialogBinding.weatherSnow to Triple("눈", R.drawable.weather_snowy_sel, 5)
        )

        // 초기 확인 버튼 비활성화
        dialogBinding.btnConfirm.isEnabled = false


        // 아이콘 클릭 시
        for ((icon, data) in weatherMap) {
            icon.setOnClickListener {
                // 기존 선택된 아이콘 원래 이미지로 되돌림
                selectedWeatherIcon?.setImageResource(getUnselectedRes(selectedWeatherIcon?.id))

                // 새 아이콘 선택 표시
                icon.setImageResource(data.second)
                selectedWeatherIcon = icon

                // 텍스트 변경
                binding.weatherText.text = data.first
                binding.weatherCircle.setImageResource(data.second)

                // selectedWeatherId 설정!
                selectedWeatherId = data.third

                // 확인 버튼 활성화
                dialogBinding.btnConfirm.isEnabled = true
                dialogBinding.btnConfirm.setBackgroundResource(R.drawable.save_btn_active)
            }
        }

        dialogBinding.btnBack.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnConfirm.setOnClickListener { dialog.dismiss() }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private var isRecording = false
    private var isPlaying = false
    private var hasRecording = false
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""

    private fun setupRecordingUI() {
        val recordCircle = binding.recordCircle         // 녹음 버튼
        val recordText = binding.recordText             // 텍스트 ("-", "녹음중" 등)
        val playBtn = binding.btnPlay                   // ▶️ 버튼
        val deleteBtn = binding.btnDelete               // ❌ 버튼

        // 초기화 상태
        playBtn.visibility = View.GONE
        deleteBtn.visibility = View.GONE

        recordCircle.setOnClickListener {
            when {
                !isRecording && !hasRecording -> {
                    isRecording = true
                    recordCircle.setImageResource(R.drawable.record_ing_button)
                    recordText.text = "녹음중"
                    playBtn.visibility = View.GONE
                    deleteBtn.visibility = View.GONE
                    startRecording() // ✅ 녹음 시작
                }

                isRecording -> {
                    isRecording = false
                    hasRecording = true
                    recordCircle.setImageResource(R.drawable.record_button)
                    recordText.visibility = View.GONE
                    playBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    stopRecording() // ✅ 녹음 종료
                }
            }
        }

        playBtn.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                playBtn.setImageResource(R.drawable.record_pause)
                playRecording() // ✅ 재생 시작
            } else {
                playBtn.setImageResource(R.drawable.record_play)
                // MediaPlayer는 중단 로직 따로 필요 (확장 가능)
            }
        }


        deleteBtn.setOnClickListener {
            // ❌ 녹음 삭제
            isPlaying = false
            hasRecording = false
            recordText.visibility = View.GONE
            recordCircle.setImageResource(R.drawable.record_button)
            playBtn.visibility = View.GONE
            deleteBtn.visibility = View.GONE
            // TODO: 녹음 파일 삭제 처리
        }
    }

    private fun startRecording() {
        val outputDir = cacheDir // 내부 저장소 (권한 필요 없음)
        val audioFile = File.createTempFile("record_", ".m4a", outputDir)
        audioFilePath = audioFile.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFilePath)
            prepare()
            start()
        }
        Log.d("녹음", "시작됨: $audioFilePath")
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Log.d("녹음", "종료됨: $audioFilePath")
    }

    private fun playRecording() {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFilePath)
            prepare()
            start()
        }
        Log.d("재생", "재생 시작됨")
    }

    //파일 업로드 관련 코드
    fun uploadFile(filePath: String, type: String, callback: (String?) -> Unit) {
        val file = File(filePath)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        ApiProvider.api.uploadFile(type, multipart).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val uploadedUrl = response.body()?.string() // ← plain text일 경우
                    Log.d("파일 업로드 성공", "[$type] $uploadedUrl")
                    callback(uploadedUrl)
                } else {
                    Log.e("파일 업로드 실패", response.errorBody()?.string().toString())
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("파일 업로드 오류", t.message.toString())
                callback(null)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun applyPhotoMetadataToTextViews(imageFile: File, context: Context) {
        val exif = ExifInterface(imageFile)

        // 날짜/시간 추출
        val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            ?: exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
            ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))

        val parts = datetime.split(" ", ":", ".")

        if (datetime.contains(" ")) {
            val (dateRaw, timeRaw) = datetime.split(" ")
            val dateParts = dateRaw.replace(":", ".").split(".")
            val timeParts = timeRaw.split(":")

            if (dateParts.size >= 3 && timeParts.size >= 2) {
                val date = "${dateParts[0]}.${dateParts[1]}.${dateParts[2]}"
                val time = "${timeParts[0]}:${timeParts[1]}"
                binding.date.text = date
                binding.time.text = time
            }
        }

        // 위치 추출
        val latLong = FloatArray(2)
        val hasLatLong = exif.getLatLong(latLong)
        Log.d("메타데이터", "hasLatLong = $hasLatLong")

        if (hasLatLong) {
            // 👉 사진에서 위치 추출 성공
            getAddressFromLatLon(binding, context, latLong[0].toDouble(), latLong[1].toDouble())

        } else {
            // 👉 사진에 위치 없음 → 현재 위치로 대체
            getCurrentLocation(binding, context)

        }
    }
}





private fun showEmotionColorDialog(context: Context) {

    val dialog = Dialog(context)
    val dialogBinding = DialogSelectEmotionColorBinding.inflate(LayoutInflater.from(context))
    dialog.setContentView(dialogBinding.root)

    // 닫기 버튼 클릭 시 다이얼로그 닫기
    dialogBinding.backToDialog1.setOnClickListener {
        dialog.dismiss()
    }

    // 저장 버튼 클릭 시 다이얼로그 닫기 (데이터 처리 없이)
    dialogBinding.dialog2SaveBtn.setOnClickListener {
        dialog.dismiss()
    }

    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.show()


}




// 선택 해제용 이미지 반환 함수
private fun getUnselectedRes(viewId: Int?): Int {
    return when (viewId) {
        R.id.weather_sunny -> R.drawable.weather_sunny_unsel
        R.id.weather_cloudy -> R.drawable.weather_cloud_unsel
        R.id.weather_rainy -> R.drawable.weather_rainy_unsel
        R.id.weather_windy -> R.drawable.weather_windy_unsel
        R.id.weather_snow -> R.drawable.weather_snowy_unsel
        else -> R.drawable.weather_sunny_unsel
    }
}


@SuppressLint("MissingPermission")
fun getCurrentLocation(binding: Record4Binding, context: Context) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                Log.d("현재위치", "lat=${location.latitude}, lon=${location.longitude}")
                getAddressFromLatLon(binding, context, location.latitude, location.longitude)
            } else {
                Log.e("현재위치", "위치를 가져올 수 없음")
            }
        }
        .addOnFailureListener {
            Log.e("현재위치", "오류: ${it.message}")
        }
}


fun getAddressFromLatLon(binding: Record4Binding, context: Context, lat: Double, lon: Double) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val cityText = address.locality ?: address.subAdminArea ?: address.adminArea ?: "알 수 없음"
            val countryText = address.countryName ?: "알 수 없음"
            val countryCode = address.countryCode ?: "KR"

            val flagEmoji = countryToFlagEmoji(countryCode)
            val displayCountry = "$flagEmoji $countryText"

            val countryCodeVal = address.countryCode ?: "KR"

// 값을 저장
            com.example.zim_android.data.network.DiaryTempStore.city = cityText
            com.example.zim_android.data.network.DiaryTempStore.countryCode = countryCodeVal


            Handler(Looper.getMainLooper()).post {
                binding.country.text = displayCountry
                binding.city.text = cityText
                Log.d("📍위치", "country=$countryText, city=$cityText")
            }

        }
    } catch (e: Exception) {
        Log.e("Geocoder", "주소 변환 실패: ${e.message}")
    }
    }
    fun countryToFlagEmoji(countryCode: String?): String {
        if (countryCode.isNullOrEmpty() || countryCode.length != 2) return "🏳️"
        return countryCode.uppercase().map {
            Character.toChars(0x1F1E6 + (it.code - 'A'.code))
        }.joinToString("") { String(it) }
    }


