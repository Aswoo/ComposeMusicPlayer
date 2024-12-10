# PlayerNotificationManager 구성 및 동작 흐름

```plaintext
+-----------------------+ 
|  PlayerNotification  |
|  Manager             |
+-----------------------+       
        |       
        |  생성
        v 
+-----------------------+ 
|  Context             | 
|  MediaDescriptionAdapter | 
|  CHANNEL_ID          | 
|  NOTIFICATION_ID     | 
+-----------------------+       
        |       
        |  setPlayer(player)       
        v 
+-----------------------+ 
|  ExoPlayer           | 
+-----------------------+       
        |       
        |  MediaDescriptionAdapter       
        v 
+-----------------------+ 
|  getCurrentContentTitle   | 
|  getCurrentContentText    | 
|  getCurrentLargeIcon      | 
|  createCurrentContentIntent | 
+-----------------------+       
        |       
        |  재생 컨트롤 액션 설정       
        v 
+-----------------------+ 
|  setUseNavigationActions   | 
|  setFastForwardIncrementMs | 
|  setRewindIncrementMs      | 
|  setStopAction             | 
+-----------------------+       
        |       
        |  알림 속성 설정       
        v 
+-----------------------+ 
|  setOngoing              | 
|  setColor                | 
|  setColorized            | 
|  setUseChronometer       | 
|  setSmallIcon            | 
|  setBadgeIconType        | 
+-----------------------+
```