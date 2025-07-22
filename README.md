# ✨ Me-mory - 감성 여행 다이어리 앱 (Frontend)

TAVE 15기 연합 프로젝트 **"Me-mory"** 프론트엔드 레포지토리입니다.  
여행의 감정, 순간, 분위기를 감성적으로 기록하고 나만의 일기로 정리할 수 있는 **맞춤형 여행 기록 앱**입니다.  

👉 [백엔드 레포 바로가기](https://github.com/TeamZim/Zim_android)  

---

## 📱 About Me-mory

> “여행의 순간을 감성적으로 기록하고, 나만의 취향과 분위기에 맞춰 정리할 수 있는 맞춤형 다이어리 앱.”

Me-mory는 사용자가 여행의 순간을 **사진, 감정, 위치, 오디오** 등 다양한 요소와 함께 기록할 수 있도록 도와주는 감성 아카이빙 서비스입니다.  
기록된 여행과 일기는 타임라인 형태로 회고할 수 있고, 방문한 국가와 감정 색을 기반으로 시각화됩니다.

---

## 🔗 Backend API 주요 기능

- **카카오 로그인 기반 사용자 인증**
- **여행 생성 / 조회 / 상세 보기**
- **일기 작성 (사진, 감정, 위치, 오디오 포함)**
- **감정 / 날씨 / 테마 목록 조회**
- **방문 국가 조회 및 지도 시각화**
- **S3 파일 업로드 / 다운로드 (Presigned URL)**

> 📎 API 문서는 Swagger UI로 제공됩니다 (추후 링크 추가)

---

## 🛠 Tech Stack

| 분야 | 기술 |
|------|------|
| Framework | Android Studio (Kotlin) |
| State Management | ViewModel, LiveData |
| Image 처리 | Glide |
| 지도 시각화 | WebView + SVG |
| 파일 업로드 | S3 Presigned URL 방식 |
| 로그인 인증 | Kakao OAuth 2.0 |
| API 연동 | Retrofit2 + Moshi |

---

## 📂 프로젝트 구조 예시

```bash
├── View/                      # 화면별 Activity, Fragment
│   ├── Onboarding/           # 온보딩 화면
│   ├── Home/                 # 홈 및 카드
│   ├── Record/               # 일기 작성 플로우
│   └── Map/                  # 지도 시각화
├── Adapter/                  # RecyclerView Adapter 등
├── data/                     # API 모델, DTO, Request/Response
├── network/                  # Retrofit API 정의
├── utils/                    # 공통 Util 함수
└── PreferenceManager.kt      # SharedPreferences 관리
