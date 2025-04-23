
### UI

| 화면 사진 1 | 화면 사진 2 | notificaiton | 가사  | GIF |
|-------------|-------------|-------------|-------------|-----|
| ![Image](https://github.com/user-attachments/assets/f8ab9e79-be91-462d-a131-59d11e4288ac) | ![KakaoTalk_Photo_2024-09-08-15-33-59](https://github.com/user-attachments/assets/3d7ecfdf-bbc1-4ff0-90c2-d8bf874da40a) | ![KakaoTalk_Photo_2024-12-30-15-47-39 001](https://github.com/user-attachments/assets/3201c893-4e38-4b95-b803-d31e1027a38b) | ![Image](https://github.com/user-attachments/assets/e9e160ba-ab0c-4309-8d80-8757c06975be) | ![화면-기록-2024-09-09-오전-10 08 29](https://github.com/user-attachments/assets/198e1e28-8f1f-4e2e-8dc2-28780e657b7a) |



### Description

Compose ui를 통해 MusicPlayer의 구성의 이해를 위한 프로젝트
Room을 이용한 Device 내 음악 정보 저장
Exoplayer를 이용하여 음악 재생,탐색,앞 뒤로 가기 설정

PlayerNotificationManager를 이용한 음악 재생 시 notification 설정

MediaSession Service를 통해 mediaSession,세션 서비스와 연동된 notification 설정

notification 에서 연결된 블루투스 기기 연동 해제,성공 시 반영하기 위한 BroadcastReceiver(ACTION_ACL_CONNECTED) 설정

BluetoothProfile.ServiceListener 로 연결된 기기 정보 연동

Lyrics API or DataBase 를 통한 가사 불러오기 및 가사 위치 추적

### Libraries

- AndroidX
  - Activity & Activity Compose
  - AppCompat
  - Core
  - Lifecycle & ViewModel Compose
  - Navigation
- Kotlin Libraries (Coroutine, Parcelize)
- Compose
  - Material3
  - Navigation
- Coil
- Dagger & Hilt
- Room
- Media3(exo player)
- ScreenShotTest(alpha)


### TODO

ScreenShotTest + UI Test + 통합 테스트 적용


