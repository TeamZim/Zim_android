package com.example.zim_android.data.network

import com.example.zim_android.data.model.*
import com.example.zim_android.fragment.ViewMapFragment
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //  인증 관련 API
    @POST("/api/login/kakao")
    fun kakaoLogin(@Body request: KakaoLoginRequest): Call<KakaoLoginResponse>

    @POST("/api/join")
    fun join(@Body request: JoinRequest): Call<UserResponse>


    // 사용자 관리 관련 API
    // 마이페이지 조회
    @GET("/api/mypage/{userId}")
    fun getMypage(@Path("userId") userId: Int): Call<MyPageResponse>

    // 로그아웃
    @PATCH("/api/setting/{userId}/logout")
    fun logout(@Path("userId") userId: Int): Call<String>

    // 회원 탈퇴
    @DELETE("/api/setting/{userId}")
    fun deleteAccount(@Path("userId") userId: Int): Call<String>


    // 여행 관리 API
    // 여행 생성
    @POST("/api/trips")
    fun createTrip(@Body request: TripCreateRequest): Call<TripResponse>

    // 여행 수정
    @PUT("/api/trips/{tripId}")
    fun updateTrip(
        @Path("tripId") tripId: Int,
        @Body request: TripUpdateRequest
    ): Call<Void>

    // 여행 목록 조회
    @GET("/api/trips")
    fun getAlltrips(): Call<List<TripResponse>>

    // 여행 상세 조회
    @GET("/api/trips/{tripId}")
    fun getTripDetail(@Path("tripId") tripId: Int): Call<TripResponse>

    // 사용자별 여행 목록 조회
    @GET("/api/trips/user/{userId}")
    fun getTripsByUser(@Path("userId") userId: Int): Call<List<TripResponse>>

    // 여행 삭제
    @DELETE("/api/trips/{tripId}")
    fun deleteTrip(@Path("tripId") tripId: Int): Call<Void>

    // 여행에 속한 다이어리들의 대표사진 목록 조회
    @GET("/api/trips/{tripId}/representative-images")
    fun getTripRepresentativeImages(@Path("tripId") tripId: Int): Call<List<TripImageResponse>>

    // 여행 대표사진 설정
    @PUT("/api/trips/{tripId}/representative-image")
    fun setTripRepresentativeImage(@Path("tripId") tripId: Int, @Body request: SetTripRepresentativeImageRequest): Call<Void>




    // 일기 관리 API
    // 일기 생성
    @POST("/api/diaries")
    fun createDiary(@Body request: DiaryCreateRequest): Call<DiaryResponse>

    // 일기 선택 필드 수정
    @PUT("/api/diaries/{diaryId}/optional-fields")
    fun updateDiaryOptionalFields(
        @Path("diaryId") diaryId: Int,
        @Body request: UpdateDiaryOptionalFieldsRequest
    ): Call<Void>

    // 대표 이미지 변경
    @PUT("/api/diaries/{diaryId}/representative-image")
    fun updateDiaryRepresentativeImage(
        @Path("diaryId") diaryId: Int,
        @Body request: UpdateDiaryRepresentativeImageRequest
    ): Call<Void>

    // 전체 일기 목록 조회
    @GET("/api/diaries")
    fun getAllDiaries(): Call<List<DiaryResponse>>

    // 일기 상세 조회
    @GET("/api/diaries/{diaryId}")
    fun getDiaryDetail(@Path("diaryId") diaryId: Int): Call<DiaryResponse>

    // 여행별 일기 목록 조회
    @GET("/api/diaries/trip/{tripId}")
    fun getDiaryByTrip(@Path("tripId") tripId: Int): Call<List<DiaryResponse>>

    // 사용자별 일기 목록 조회
    @GET("/api/diaries/user/{userId}")
    fun getDiariesByUser(@Path("userId") userId: Int): Call<List<DiaryResponse>>

    // 일기 삭제
    @DELETE("/api/diaries/{diaryId}")
    fun deleteDiary(@Path("diaryId") diaryId: Int): Call<Void>


    // 국가 관리 API
    // 국가 검색
    @GET("/api/countries/search")
    fun searchCountry(@Query("keyword") keyword: String): Call<List<CountrySearchResponse>>

    // 방문 국가 저장
    @POST("/api/countries/{userId}")
    fun addVisitedCountry(
        @Path("userId") userId: Int,
        @Body request: AddVisitedCountryRequest
    ): Call<Void>


    // 특정 사용자의 방문 국가 전체 조회
    @GET("/api/countries/{userId}")
    fun getVisitedCountries(@Path("userId") userId: Int): Call<ViewMapFragment.VisitedCountryListResponse>

    // 방문 국가 삭제
    @DELETE("/api/countries/{userId}")
    fun deleteVisitedCountry(
        @Path("userId") userId: Int,
        @Query("countryCode") countryCode: String
    ): Call<Void>



    // 감정 관리 API
    // 감정 목록 조회
    @GET("/api/emotions")
    fun getEmotions(): Call<List<Emotion>>



    // 날씨 관리 API
    // 날씨 목록 조회
    @GET("/api/weathers")
    fun getWeatherList(): Call<List<WeatherResponse>>

    // 특정 날씨 조회
    @GET("/api/weathers/{weatherId}")
    fun getSpecificWeather(@Path("weatherId") weatherId: Int): Call<WeatherResponse>


    // 테마 목록 받아오기
    @GET("/api/themes")
    fun getThemelist(): Call<List<Theme>>


    // 파일 업로드 API
    // 파일 업로드
    // Retrofit에서 이미지나 오디오 같은 바이너리 파일을 업로드할 땐 무조건 @Multipart를 써야함.
    @Multipart
    @POST("/upload")
    fun uploadFile(
        @Query("type") type: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>


}