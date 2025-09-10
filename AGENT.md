# Agent Guide

## 1. 프로젝트 구조 (Project Structure)

*   **MainActivity 파일 경로:** `app/src/main/java/.../MainActivity.kt`
*   **Navigation 구성:** `app/src/main/java/.../ui/Navigation.kt` (Jetpack Navigation Component 사용 권장)

## 2. 아키텍처 규칙 (Architecture Rules)

*   **기본 패턴:** Unidirectional Data Flow (UDF), MVVM 패턴 준수.
*   **ViewModel:**
    *   모든 비즈니스 로직 및 UI 상태 관리는 ViewModel에 위치시킨다.
    *   ViewModel 클래스명은 `ScreenNameViewModel` 형식을 따른다. (예: `UserProfileViewModel`)
*   **Repository 패턴:**
    *   데이터 접근 로직(네트워크, 로컬 DB 등)은 Repository 패턴을 사용하여 ViewModel과 분리한다.
    *   ViewModel은 Repository를 통해 데이터와 상호작용한다.
*   **상태 관리 (State Management):**
    *   Jetpack Compose 환경에서 ViewModel의 UI 상태는 `StateFlow`를 사용하여 노출한다.
    *   UI 이벤트는 `SharedFlow` 또는 ViewModel 함수를 통해 처리한다.
*   **의존성 주입 (Dependency Injection):**
    *   Hilt를 사용하여 의존성을 주입한다.
    *   생성자 주입(Constructor Injection)을 우선적으로 사용한다.

## 3. UI 및 스타일 가이드 (UI & Style Guide)

*   **UI 프레임워크:** UI는 반드시 Jetpack Compose를 사용하여 구현한다. XML 레이아웃 사용은 권장하지 않는다.
*   **Composable 함수명:** `PascalCase`를 사용한다. (예: `UserProfileCard`)
*   **Kotlin 코딩 컨벤션:** [Kotlin 공식 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html)을 준수한다.
*   **명명 규칙:**
    *   함수명: `camelCase` (기존 규칙 유지)
    *   리소스 ID (XML이 아닌 Compose 내 식별자 등): `snake_case` (기존 규칙 유지)
    *   상수 (Constants): `UPPER_SNAKE_CASE` (예: `MAX_USER_COUNT`)
*   **Linter & Formatter:**
    *   `ktlint`를 사용하여 코드 스타일을 검사하고 일관성을 유지한다.
    *   IDE의 Formatter 설정을 프로젝트 레벨에서 통일한다. (Kotlin Style Guide 권장)
*   **주석:**
    *   공개 API, 복잡한 로직, 수정 필요한 부분(`TODO:`, `FIXME:`)에 명확한 주석을 작성한다.

## 4. 네트워크 (Networking)

*   네트워크 요청은 Retrofit 라이브러리를 기반으로 구현한다.
*   응답 처리는 `Result` Wrapper 또는 유사한 패턴을 사용하여 성공/실패 케이스를 명확히 한다.

## 5. 리소스 관리 (Resource Management)

*   **문자열:** 모든 사용자 표시 문자열은 `strings.xml`에 정의하고 사용한다. 하드코딩 금지.
*   **크기 (Dimensions):** UI 요소의 크기, 간격 등은 `dimens.xml`에 정의하고 사용한다.
*   **색상 (Colors):** 앱에서 사용되는 색상은 `colors.xml` 또는 테마별 색상 파일(`ui/theme/Color.kt` in Compose)에 정의하고 사용한다.
*   **이미지:**
    *   SVG 사용을 우선적으로 고려한다.
    *   PNG/JPG 사용 시, 최적화된 리소스를 사용한다. WebP 형식 사용도 고려한다.

## 6. 테스트 (Testing)

*   **단위 테스트 (Unit Tests):**
    *   ViewModel, Repository, UseCase 등 비즈니스 로직을 포함하는 클래스는 JUnit과 MockK(또는 Mockito)를 사용하여 단위 테스트를 작성한다.
    *   테스트 커버리지 목표: 주요 로직 70% 이상 (프로젝트 상황에 따라 조절)
*   **UI 테스트 (UI Tests):**
    *   주요 사용자 플로우에 대해 Jetpack Compose 테스트 라이브러리를 사용하여 UI 테스트를 작성한다.

## 7. 버전 관리 및 협업 (Version Control & Collaboration)

*   **Git 브랜치 전략:** Git Flow 또는 GitHub Flow를 따른다
*   **커밋 메시지 컨벤션:** Conventional Commits 양식을 사용하여 커밋 메시지를 작성한다. (예: `feat: Add user login feature`)

## 8. 라이브러리 (Libraries)

*   주요 라이브러리 버전은 프로젝트 레벨 `build.gradle` 또는 `libs.versions.toml`에서 관리하여 통일성을 유지한다.
*   새로운 라이브러리 도입 시 코드 리뷰 및 승인 과정을 거친다.

## 9. 문서화 (Documentation)

*   복잡한 아키텍처 결정 사항, 공유 모듈의 API, 설정 방법 등은 프로젝트 내 `README.md` 또는 Wiki에 문서화한다.
*   KDoc을 활용하여 공개 API 및 주요 함수에 대한 설명을 작성한다.

---

**팀 공용 규칙 (기존 내용):**

*   함수명은 `camelCase`를 사용.
*   리소스 ID는 `snake_case`로 표기. (Compose 환경에서는 Composable 내부의 식별자 등에 해당될 수 있음)
