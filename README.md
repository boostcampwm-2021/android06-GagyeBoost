 
# 가계Booooost💸🚀 (Android06 - 코틀린이)

![](https://i.imgur.com/nNXlz30.gif)


<div align="center">
    
[![Android](https://img.shields.io/badge/android%20studio-2020.3.1%20Patch%203-%233DDC84?logo=android-studio)]() [![Kotlin](https://img.shields.io/badge/kotlin-1.5.31-%237F52FF?logo=kotlin)]()
    
[![DI](https://img.shields.io/badge/DI-koin-%2358C8AE)]() [![Pattern](https://img.shields.io/badge/Pattern-MVVM-%2358C8AE)]() [![Google Maps](https://img.shields.io/badge/Library-Google%20Maps-%23%234285F4?logo=googlemaps)]() [![Retrofit](https://img.shields.io/badge/Library-Retrofit-%23%234285F4)]() [![Firebase](https://img.shields.io/badge/Tool-Firebase-%23FFCA28?logo=firebase)]()
</div>
 
<br>

## 🖥 주요 화면


| 홈화면 | 통계 | 지도 | 검색 |
| :--------: | :--------: | :--------: |:--------: |
|<img src="https://user-images.githubusercontent.com/83066991/144200115-aaa62b75-aa3c-42c3-a85d-9d5618371a55.gif" width="200">|<img src="https://user-images.githubusercontent.com/83066991/144200224-557a71a3-af10-4639-81d3-d71b8e99074e.gif" width="200">|<img src="https://user-images.githubusercontent.com/83066991/144199996-d3440795-1066-4490-a57e-feef5bfa0f6a.gif" width="200">|<img src="https://user-images.githubusercontent.com/83066991/144199614-dff7f63b-8b4a-440c-a2d0-e72dc12afc8a.gif" width="200">|

<br>

## 🌟 화면 별 기능
### 홈화면
- 월별 달력으로 일자별 지출/수입 내역 조회
- 원하는 날짜에 데이터 추가
- 카테고리 선택 가능

### 통계
- 지출/수입 별 월별 통계
- 지출/수입 별 일별 추이

### 지도
- 지출/수입이 있었던 위치 확인 및 세부내용 확인 가능
- 원하는 카테고리, 기간 별 지출/수입 지정

### 검색
- 조건에 맞는 기록만 조회 (수입/지출, 내역, 금액, 카테고리)

<br>

## 🤼‍♂️ 팀원 🤼‍♀️
|[K009 김영훈](https://github.com/kim0hoon)|[K011 김지현](https://github.com/7hong13)|[K032 신재혁](https://github.com/sus0985)|[K034 안하현](https://github.com/anhahyoun)|
|:----:|:----:|:----:|:----:|
|<img src="https://github.com/kim0hoon.png" width="100">|<img src="https://github.com/7hong13.png" width="100">|<img src="https://github.com/sus0985.png" width=100>|<img src="https://github.com/anhahyoun.png" width="100">|

<br>

## ✨ 기술 스택
![](https://i.imgur.com/9u3SWYu.png)

<br>

## 💡 프로젝트 주요 기술

1️⃣ Custom Calendar 구현
  
2️⃣ 의존성 주입으로 Koin 사용

3️⃣ 마커 클러스터링
<br>

## 👊 프로젝트 도전 과제

- 매주 버그 없이 테스트 배포
- 라이브러리 사용 없이 Custom 달력 구현
- 마커 클러스터링 최적화
- 코드 리뷰
<br>

## 📂 Project Folder
```
📂gagyeboost
  ├─common
  ├─di
  ├─model
  │  ├─data
  │  ├─local
  │  ├─remote
  │  └─repository.kt
  └─ui
     ├─address
     ├─base
     ├─home
     ├─intro
     ├─map
     ├─search
     └─statstics
```

<details>
<summary><b>UI 파일 구조 자세히 보기</b></summary>
    
```
📂 UI
├── MainActivity.kt
├── 📂 address
│   ├── AddressResultActivity.kt
│   └── AddressResultViewModel.kt
├── 📂 base
│   ├── BaseActivity.kt
│   └── BaseFragment.kt
├── 📂 home
│   ├── AddViewModel.kt
│   ├── CustomCalendar.kt
│   ├── CustomCalendarAdapter.kt
│   ├── HomeFragment.kt
│   ├── HomeViewModel.kt
│   ├── NumberPickerDialog.kt
│   ├── 📂 add
│   │   └── AddFragment.kt
│   ├── 📂 category
│   │   ├── CategoryAdapter.kt
│   │   └── CategoryFragment.kt
│   ├── 📂 categoryControl
│   │   ├── AddCategoryFragment.kt
│   │   ├── CategoryIconAdapter.kt
│   │   ├── CategoryIconListFragment.kt
│   │   └── UpdateCategoryFragment.kt
│   ├── 📂 detail
│   │   ├── DateDetailAdapter.kt
│   │   ├── RecordDetailActivity.kt
│   │   └── RecordDetailViewModel.kt
│   └── 📂 selectPosition
│       ├── AddressAdapter.kt
│       ├── LoadStateAdapter.kt
│       └── SelectPositionFragment.kt
├── 📂 intro
│   └── IntroActivity.kt
├── 📂 map
│   ├── ClusterRender.kt
│   ├── DetailAdapter.kt
│   ├── MapDetailFragment.kt
│   ├── MapFragment.kt
│   ├── MapViewModel.kt
│   └── 📂 filter
│       ├── FilterCategoryAdapter.kt
│       ├── FilterCategoryDialog.kt
│       └── FilterMoneyDialog.kt
├── 📂 search
│   ├── SearchCategoryAdapter.kt
│   ├── SearchCategoryDialog.kt
│   ├── SearchFragment.kt
│   ├── SearchResultFragment.kt
│   └── SearchViewModel.kt
└── 📂 statstics
    ├── CustomPieNumberFormatter.kt
    ├── StatResultAdapter.kt
    ├── StatisticsFragment.kt
    └── StatisticsViewModel.kt
```
    
</details>


## 🎞 데모 영상
<a href="https://www.youtube.com/watch?v=9q4E_THkU9g">
    <img src="https://i.imgur.com/P6Kh8qx.png"/></a>
<br>
  
## 🔗 관련 링크
- [Ground rule](https://cuddly-fir-e09.notion.site/39dffbe249be45599aac0e1db06d4c25)
- [Backlog](https://docs.google.com/spreadsheets/d/1DGLHnlF7mAqhZZ-C_aFjou7Zl4KE4YrJlnghAUXCOyg/edit#gid=0)
- [Networking day: 프로젝트 상세 정리](https://wind-knight-242.notion.site/13-50-Android06-4ff90ddee7b4440da524ea514fd892c2)
<br>
