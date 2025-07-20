package com.example.zim_android.View

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.Adapter.OnBoardingAdapter
import com.example.zim_android.MainActivity
import com.example.zim_android.PreferenceManager
import com.example.zim_android.R
import com.example.zim_android.databinding.ActivityOnboardingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.zim_android.data.model.JoinRequest
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import android.util.Log
import com.example.zim_android.data.model.User
import com.example.zim_android.data.network.ApiProvider
import com.example.zim_android.data.model.UserResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause




class OnBoardingActivity : AppCompatActivity() {

    private lateinit var savedBirthForApi: String
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }


    private lateinit var savedKakaoId: String

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnBoardingAdapter
    private var selectedImageView: ImageView? = null

    private val pageImages = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4,
        R.drawable.onboarding_5,
        R.drawable.onboarding_6,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = OnBoardingAdapter(pageImages)
        binding.onboardingViewPager.adapter = adapter

        setupIndicator()
        setCurrentIndicator(0)

        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)



                // ✅ 입력 안 끝났으면 스와이프 막기
                binding.onboardingViewPager.isUserInputEnabled = when (position) {
                    3,4 -> false  // 3,4번 페이지에서 잠금
                    else -> true
                }

                when (position) {
                    in 0..2 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.next_big_but)
                    }
                    3 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.kakao_but)
                    }
                    4 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.next_big_but_non)

                        binding.onboardingViewPager.post {
                            setupPhotoPicker()
                            observeInputFields()
                        }
                    }

                    5 -> {
                        binding.nextButton.setBackgroundResource(R.drawable.start_big_but)
                    }
                }
            }
        })



        binding.nextButton.setOnClickListener {
            val currentPage = binding.onboardingViewPager.currentItem


            // 👉 카카오 로그인 페이지일 때
            if (currentPage == 3) {
                startKakaoLogin()
                return@setOnClickListener
            }

            // 4번 페이지일 경우: 필드 입력 안되면 클릭 무시
            if (currentPage == 4) {
                val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
                    ?.findViewHolderForAdapterPosition(4)?.itemView ?: return@setOnClickListener

                val koreanNameEdit = currentView.findViewById<EditText>(R.id.koreanNameEdit)
                val birthdayEdit = currentView.findViewById<EditText>(R.id.birthdayEdit)
                val lastNameEdit = currentView.findViewById<EditText>(R.id.lastNameEngEdit)
                val firstNameEdit = currentView.findViewById<EditText>(R.id.firstNameEngEdit)

                val allFilled = listOf(
                    koreanNameEdit.text?.isNotBlank(),
                    birthdayEdit.text?.isNotBlank(),
                    lastNameEdit.text?.isNotBlank(),
                    firstNameEdit.text?.isNotBlank()
                ).all { it == true }

                if (!allFilled) return@setOnClickListener // 👉 입력 안되면 클릭 무시


                val imageView = currentView.findViewById<ImageView>(R.id.photoUploadBackground)
                val imageUri = imageView.tag as? Uri ?: return@setOnClickListener

                val filePath = getRealPathFromUri(imageUri)
                val imageFile = File(filePath)
                val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val multipart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                ApiProvider.api.uploadFile("profile", multipart)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val rawJson = response.body()?.string()
                                Log.d("업로드 응답", rawJson ?: "null")

                                try {
                                    val imageUrl = rawJson?.replace("\"", "") ?: return
                                    sendJoinRequest(imageUrl)
                                } catch (e: Exception) {
                                    Log.e("파싱 오류", e.message ?: "unknown error")
                                }

                            } else {
                                Log.e("이미지 업로드 실패", "서버 응답 오류: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("이미지 업로드 실패", t.message ?: "unknown error")
                        }
                    })



                return@setOnClickListener
            }

            // 페이지 이동
            val nextIndex = currentPage + 1
            if (nextIndex < adapter.itemCount) {
                binding.onboardingViewPager.currentItem = nextIndex
            } else {
                PreferenceManager.setOnboardingShown(this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
        }
    }


    private fun setupPhotoPicker() {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

        val photoUploadBox = currentView.findViewById<FrameLayout>(R.id.photoUploadBox)
        val photoUploadBackground = currentView.findViewById<ImageView>(R.id.photoUploadBackground)

        selectedImageView = photoUploadBackground // ← 여기다 이미지 넣을 거야

        photoUploadBox.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            val imageUri = data?.data

            // 현재 뷰에서 아이콘, 텍스트 찾아오기
            val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
                ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

            val uploadIcon = currentView.findViewById<ImageView>(R.id.uploadIcon)
            val uploadText = currentView.findViewById<TextView>(R.id.uploadText)
            val cancelBtn = currentView.findViewById<ImageButton>(R.id.cancelImage)
            val uploadImage = currentView.findViewById<ImageView>(R.id.photoUploadBackground)

            // 아이콘과 텍스트 안 보이게
            uploadIcon.visibility = View.GONE
            uploadText.visibility = View.GONE
            cancelBtn.visibility = View.VISIBLE
            uploadImage.visibility = View.VISIBLE

            cancelBtn.setOnClickListener {
                // 이미지 제거
                uploadImage.setImageDrawable(null)
                uploadImage.visibility = View.GONE

                // 아이콘과 텍스트 다시 표시
                uploadIcon.visibility = View.VISIBLE
                uploadText.visibility = View.VISIBLE

                // 취소 버튼 숨기기
                cancelBtn.visibility = View.GONE
            }


            // 업로드한 이미지 표시

            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions().centerCrop().transform(RoundedCorners(32)))
                .into(uploadImage)
            uploadImage.tag = imageUri

        }
    }


    private fun observeInputFields() {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return



        // 대문자만 입력되도록 강제
        val toUpperFilter = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text != text.uppercase()) {
                    s?.replace(0, s.length, text.uppercase())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }




        val koreanNameEdit = currentView.findViewById<EditText>(R.id.koreanNameEdit)
        val birthdayEdit = currentView.findViewById<EditText>(R.id.birthdayEdit)
        val lastNameEdit = currentView.findViewById<EditText>(R.id.lastNameEngEdit)
        val firstNameEdit = currentView.findViewById<EditText>(R.id.firstNameEngEdit)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updatePage4ButtonState(koreanNameEdit, birthdayEdit, lastNameEdit, firstNameEdit)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        //생일 입력시 드르륵 하게 하는
        birthdayEdit.isFocusable = false
        birthdayEdit.isClickable = true
        birthdayEdit.setOnClickListener {
            showDatePickerDialog(birthdayEdit)
        }

        lastNameEdit.addTextChangedListener(toUpperFilter)
        firstNameEdit.addTextChangedListener(toUpperFilter)
        koreanNameEdit.addTextChangedListener(watcher)
        birthdayEdit.addTextChangedListener(watcher)
        lastNameEdit.addTextChangedListener(watcher)
        firstNameEdit.addTextChangedListener(watcher)
    }

    private fun updatePage4ButtonState(
        koreanEdit: EditText,
        birthEdit: EditText,
        lastEdit: EditText,
        firstEdit: EditText
    ) {
        val allFilled = listOf(
            koreanEdit.text?.isNotBlank(),
            birthEdit.text?.isNotBlank(),
            lastEdit.text?.isNotBlank(),
            firstEdit.text?.isNotBlank()
        ).all { it == true }

        binding.nextButton.setBackgroundResource(
            if (allFilled) R.drawable.next_big_but else R.drawable.next_big_but_non
        )


    }

    private fun setupIndicator() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layout = binding.indicatorLayout
        layout.removeAllViews()

        for (i in indicators.indices) {
            indicators[i] = ImageView(this).apply {
                setImageResource(R.drawable.dot_inactive)
                val params = LinearLayout.LayoutParams(24, 24)
                params.setMargins(8, 0, 8, 0)
                layout.addView(this, params)
            }
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val layout = binding.indicatorLayout
        for (i in 0 until layout.childCount) {
            val imageView = layout.getChildAt(i) as ImageView

            if (i == index) {
                imageView.setImageResource(R.drawable.icon_plane)
                val width = (27 * resources.displayMetrics.density).toInt()
                val height = (26 * resources.displayMetrics.density).toInt()
                imageView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
                    setMargins(8, 0, 8, 0)
                }
            } else {
                imageView.setImageResource(R.drawable.dot_inactive)
                val size = (10 * resources.displayMetrics.density).toInt()
                imageView.layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(12, 0, 12, 0)
                }
            }
        }
    }

    //드르륵
    private fun showDatePickerDialog(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this@OnBoardingActivity,
            android.R.style.Theme_Holo_Light_Dialog,
            { _, year, monthOfYear, dayOfMonth ->

                // 1. UI에 보이는 예쁜 포맷
                val monthNames = listOf(
                    "1월/Jan", "2월/Feb", "3월/Mar", "4월/Apr", "5월/May", "6월/Jun",
                    "7월/Jul", "8월/Aug", "9월/Sep", "10월/Oct", "11월/Nov", "12월/Dec"
                )
                val dayStr = "%02d".format(dayOfMonth)
                val birthFormattedForDisplay = "$dayStr ${monthNames[monthOfYear]} $year"
                targetEditText.setText(birthFormattedForDisplay)

                // 2. 서버에 보낼 ISO 포맷
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                val serverFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                val birthFormattedForServer = serverFormat.format(calendar.time)

                // 3. 서버에 보낼 값 따로 저장해두기 (예: 전역 변수에 저장)
                savedBirthForApi = birthFormattedForServer // 예: lateinit var savedBirthForApi: String

            },
            year, month, day
        )

        datePickerDialog.show()

    }

    private fun sendJoinRequest(imageUrl: String) {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

        val koreanName = currentView.findViewById<EditText>(R.id.koreanNameEdit).text.toString()
        val birthday = currentView.findViewById<EditText>(R.id.birthdayEdit).text.toString()
        val lastName = currentView.findViewById<EditText>(R.id.lastNameEngEdit).text.toString()
        val firstName = currentView.findViewById<EditText>(R.id.firstNameEngEdit).text.toString()

        val request = JoinRequest(
            kakaoId = savedKakaoId,
            profileImageUrl = imageUrl,
            surName = lastName,
            firstName = firstName,
            koreanName = koreanName,
            birth = savedBirthForApi,
            nationality = "REPUBLIC OF KOREA"
        )

        Log.d("회원가입", "요청 보냄: $savedKakaoId / $koreanName / $savedBirthForApi / $imageUrl / $lastName / $firstName ")


        ApiProvider.api.join(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    // ✅ ViewPager를 5페이지로 이동
                    binding.onboardingViewPager.currentItem = 5

                    // ✅ 온보딩 표시 여부 저장은 여기서 하지 않음 (사용자가 "시작하기" 눌러야 저장)
                    // PreferenceManager.setOnboardingShown(this@OnBoardingActivity)
                    // startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
                    // finish()
                    Log.d("로그/업로드", "응답 코드: ${response.code()}")


                } else {
                    Log.e("회원가입 실패", "응답 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("회원가입 실패", t.message ?: "unknown error")
            }
        })

    }

    private fun getRealPathFromUri(uri: Uri): String {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        throw IllegalArgumentException("이미지 경로를 가져올 수 없습니다.")
    }

    private fun uploadProfileImage(imageFile: File, callback: (String?) -> Unit) {
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

        ApiProvider.api.uploadFile("profile", body)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val json = response.body()?.string()
                        Log.d("프로필 업로드 응답", json ?: "응답 없음")

                        try {
                            val gson = Gson()
                            val userResponse = gson.fromJson(json, User::class.java)
                            val imageUrl = userResponse.profileImageUrl
                            callback(imageUrl)
                        } catch (e: Exception) {
                            Log.e("파싱 실패", e.message ?: "unknown error")
                            callback(null)
                        }
                    } else {
                        Log.e("프로필 업로드 실패", "응답 코드: ${response.code()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("프로필 업로드 오류", t.message ?: "알 수 없음")
                    callback(null)
                }
            })

    }

    private fun startKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("카카오 로그인", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("카카오 로그인", "로그인 성공 ${token.accessToken}")

                // ✅ 사용자 정보 요청
                UserApiClient.instance.me { user, meError ->
                    if (meError != null) {
                        Log.e("카카오 사용자 정보", "사용자 정보 요청 실패", meError)
                    } else if (user != null) {
                        val kakaoId = user.id?.toString() ?: return@me
                        val imageUrl = user.kakaoAccount?.profile?.profileImageUrl

                        Log.d("카카오 사용자 정보", "kakaoId: $kakaoId")
                        Log.d("카카오 사용자 정보", "profileImageUrl: $imageUrl")

                        // 🔥 전역 변수에 저장해서 나중에 회원가입 때 사용
                        savedKakaoId = kakaoId

                        if (!imageUrl.isNullOrBlank()) {
                            showKakaoProfileImageOnPage4(imageUrl)
                        }

                        binding.onboardingViewPager.currentItem = 4
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(context = this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context = this, callback = callback)
        }
    }



    private fun showKakaoProfileImageOnPage4(imageUrl: String) {
        val currentView = (binding.onboardingViewPager.getChildAt(0) as? RecyclerView)
            ?.findViewHolderForAdapterPosition(4)?.itemView ?: return

        val uploadIcon = currentView.findViewById<ImageView>(R.id.uploadIcon)
        val uploadText = currentView.findViewById<TextView>(R.id.uploadText)
        val cancelBtn = currentView.findViewById<ImageButton>(R.id.cancelImage)
        val uploadImage = currentView.findViewById<ImageView>(R.id.photoUploadBackground)

        // 기본 아이콘 숨기고 이미지 표시
        uploadIcon.visibility = View.GONE
        uploadText.visibility = View.GONE
        cancelBtn.visibility = View.VISIBLE
        uploadImage.visibility = View.VISIBLE

        // Glide로 이미지 표시
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions().centerCrop().transform(RoundedCorners(32)))
            .into(uploadImage)

        // 업로드 취소 기능
        cancelBtn.setOnClickListener {
            uploadImage.setImageDrawable(null)
            uploadImage.visibility = View.GONE
            uploadIcon.visibility = View.VISIBLE
            uploadText.visibility = View.VISIBLE
            cancelBtn.visibility = View.GONE
        }

        // tag에도 저장해두기 → 이후 회원가입 시 이미지 업로드 안 하고 바로 URL 쓰기 가능
        uploadImage.tag = imageUrl
        Log.d("카카오 사용자 이미지", "imageUrl: $imageUrl")

    }


}



