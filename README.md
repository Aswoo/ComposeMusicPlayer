# 🎵 Compose Music Player

> Jetpack Compose 기반 Android 음악 플레이어  
> ExoPlayer 및 Room, MediaSession, Notification 기능을 활용한 프로젝트

---

## 📸 UI Preview

| Main Player | PlayList | Notification | Lyrics |
|-------------|----------------|---------------|--------|
| ![Main Player](https://github.com/user-attachments/assets/a2c27510-57d8-45bd-ba4f-1cdd2f031f52) | ![PlayList](https://github.com/user-attachments/assets/deb1d1bf-87b2-4c2c-a42e-2fdbe4c3e273) | ![Notification](https://github.com/user-attachments/assets/3201c893-4e38-4b95-b803-d31e1027a38b) | ![Lyrics](https://github.com/user-attachments/assets/e9e160ba-ab0c-4309-8d80-8757c06975be) |

| DetailPlayList | Add PlayList | Playing | (Reserved) |
|----------|-----------|-----------|------------|
| ![Detail PlayList GIF](https://github.com/user-attachments/assets/d3232673-1fdc-4a9f-a528-cb11bc2e77ca) | ![Add PlayList](https://github.com/user-attachments/assets/eb4b50ce-ef34-41c7-b7f4-1a77b843327d) | ![Playing](https://github.com/user-attachments/assets/c8801681-c65f-4a8e-8476-448cfdca47cd) |   |

## 🧩 Features

- 🎼 **Jetpack Compose** 기반 UI
- 🎵 **ExoPlayer**를 통한 고성능 음악 재생
- 💽 **Room**을 통한 기기 내 음악 정보 저장 및 관리
- 📲 **MediaSession + PlayerNotificationManager**로 미디어 제어 알림 설정
- 🎧 블루투스 연결 상태 감지 및 처리 (`BroadcastReceiver`, `BluetoothProfile.ServiceListener`)
- 📝 **가사 API 연동**으로 실시간 가사 추적
- 📁 **플레이리스트 기반 재생 기능**
- 🎛️ 재생/일시정지/앞뒤 탐색 등 플레이어 컨트롤

---

## ⚙️ Architecture

- MVVM + Clean Architecture 구성
- Hilt 기반 DI 구성
- ViewModel → StateFlow 기반 상태 관리
- 단일 재생 / 플레이리스트 재생 지원

---

## 📚 Tech Stack

### 🔧 Core Libraries
- **Kotlin**, **Coroutines**
- **Jetpack Compose**
    - Material3
    - Navigation
- **AndroidX**
    - Activity Compose, Lifecycle, ViewModel
    - Core, AppCompat

### 💾 Data
- **Room**
- **Parcelize**

### 🔊 Media
- **Media3 (ExoPlayer)**
- **PlayerNotificationManager**
- **MediaSession**

### 💉 Dependency Injection
- **Hilt (Dagger)**

### 🖼️ UI
- **Coil** (이미지 로딩)
- **Screenshot Testing** (alpha)

---

## ✅ TODO

- [ ] Screenshot 테스트 적용
- [ ] UI 테스트 추가
- [ ] 통합 테스트 도입

---

## 📂 프로젝트 목적

- Jetpack Compose 학습 및 아키텍처 이해
- ExoPlayer 및 Notification/MediaSession 연동 구현
- 로컬 음악 기반 미디어 재생 UX 개선 실험
- 가사 API 및 음악 메타데이터 처리 경험
- 테스트 코드 및 Compose 기반 UI 테스트 적용 실습

---

## 🧪 실행 방법

1. Android Studio Giraffe 이상 권장
2. 로컬 기기에 오디오 파일이 존재해야 정상 동작
3. `MediaSessionService`와 권한 설정 필요
4. 가사 API Key 설정 필요 X

---

## ✨ Recent Changes
블루투스 이어폰 연동 및 UI 동기화 개선
기존 음악 재생 관리 로직을 개선하여, 블루투스 이어폰에서 발생하는 재생, 일시정지, 트랙 변경 등의 기능이 앱 UI와 정확하게 동기화되도록 수정했습니다.
이를 통해 이어폰 버튼 조작 시 UI 상태와 실제 재생 상태가 일치하며, 오디오 파형(AudioWave) 관련 동작 에러도 함께 해결하여 재생 중 파형 표시가 안정적으로 작동합니다.

TODO
블루투스 이어폰 좌/우 두 번 클릭(더블탭) 기능 지원 여부 조사 및 대응

인터넷 조사 결과, 삼성 갤럭시 버즈 시리즈에서는 좌/우 더블탭 기능에 대한 공식 지원이 제한적이며, 일부 사용자 커뮤니티에서도 기능이 불완전하거나 지원하지 않는 것으로 보고됨

반면, 애플 에어팟은 더블탭을 통한 트랙 넘김, 재생/일시정지 기능이 명확히 지원됨

참고 링크:

Samsung Galaxy Buds Gesture Controls Limitations - Reddit Discussion

Apple AirPods Gesture Controls Explained - Apple Support