plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.screenshot)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.compiler)

    id("kotlin-parcelize")
    // ktlint 플러그인 추가
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.sdu.composemusicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sdu.composemusicplayer"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/NOTICE"
        }
    }

    configurations {
        implementation {
            exclude(group = "org.jetbrains", module = "annotations")
        }
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    sourceSets {
        create("screenshotTest") {
            java.srcDir("src/screenshotTest/java") // 스크린샷 테스트 코드 경로
            res.srcDir("src/screenshotTest/res") // 리소스 경로 (필요 시)
            manifest.srcFile("src/screenshotTest/AndroidManifest.xml") // 매니페스트 경로
        }
    }
}

// ktlint 설정 추가
ktlint {
    android.set(true) // Android 코드에 대해 실행하도록 설정 (필요시)
    debug.set(true) // 디버그 모드 활성화 (옵션)
    outputToConsole.set(true) // 콘솔에 출력하도록 설정
    outputColorName.set("RED") // 출력 색상 설정
    ignoreFailures.set(false) // 실패 시 빌드 실패하도록 설정
    // test 코드는 검사에서 제외
    filter {
        exclude("**/test/**")
        exclude("**/androidTest/**")
        exclude("**/screenshotTest/**")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(
        libs
            .androidx
            .lifecycle
            .runtime
            .ktx,
    )
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(
        libs
            .androidx
            .ui
            .tooling
            .preview,
    )
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)
    implementation(
        libs
            .androidx
            .ui
            .tooling
            .preview
            .android,
    )
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material3.window.size)

    // Screen Compose
    screenshotTestImplementation(platform(libs.androidx.compose.bom))
    screenshotTestImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(
        libs
            .androidx
            .ui
            .test
            .junit4,
    )

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(
        libs
            .androidx
            .ui
            .test
            .manifest,
    )

    implementation(libs.androidx.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.accompanist.navigation)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    implementation(libs.hilt.navigation.compose)
    implementation(libs.accompanist.permission)

    implementation(libs.motion.layout)
    implementation(libs.material.icon)

    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)

    implementation(libs.composereorderable)

    implementation(libs.media3.exoplayer)
    implementation("androidx.compose.material:material:1.2.0")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    implementation(libs.jaudio.tagger)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    // Hilt testing
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)

    // Compose testing
    androidTestImplementation(
        libs
            .androidx
            .ui
            .test
            .junit4,
    )
    debugImplementation(
        libs
            .androidx
            .ui
            .test
            .manifest,
    )

    // Hilt Compose Testing
    androidTestImplementation(libs.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android)

    androidTestImplementation(libs.mockk.android)


    implementation(libs.annotations)
}
