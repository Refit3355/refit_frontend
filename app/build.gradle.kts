plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.refit.app"
    compileSdk = 36

    packaging {
        resources {
            // 멀티-릴리즈 JAR 영역 전체 제외 (안드로이드에서 필요 없음)
            excludes += "META-INF/versions/**"

            // (옵션) 흔한 충돌 메타데이터도 함께 제외
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE*",
                "META-INF/LICENSE*",
                "META-INF/*.kotlin_module"
            )
        }
    }

    defaultConfig {
        applicationId = "com.refit.app"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["KAKAO_APP_KEY"] = project.findProperty("KAKAO_APP_KEY") ?: ""
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.i18n)
    implementation(libs.identity.android.legacy)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.9.3")

    // 헬스 데이터
    implementation("androidx.health.connect:connect-client:1.2.0-alpha01")

    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("io.coil-kt:coil-compose:2.6.0")

    // 아이콘 추가
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // 찜 저장
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // 날씨
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // 아이콘
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // 차트
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 권한 요청
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    
    // 암호화 SharedPrefences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // viewModelScope / viewModel() 컴포즈에서 쓰니 함께 권장
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.19.0")
}