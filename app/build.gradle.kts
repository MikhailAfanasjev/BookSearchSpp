plugins {
    id("com.android.application")
    //alias(libs.plugins.android.application)
    //alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.linguareader"
    compileSdk = 34

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"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\"")
        applicationId = "com.example.linguareader"
        minSdk = 29
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation ("androidx.compose.ui:ui-tooling-preview:1.3.0") // Для Preview

    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    implementation(libs.androidx.core.ktx)  // Основная библиотека Android KTX
    implementation(libs.androidx.lifecycle.runtime.ktx)  // Жизненный цикл KTX
    implementation(libs.androidx.activity.compose)  // Поддержка компонентов Activity для Compose
    implementation(platform(libs.androidx.compose.bom))  // BOM для управления версиями Compose
    implementation(libs.androidx.ui)  // Основной модуль Compose UI
    implementation(libs.androidx.ui.graphics)  // Библиотека графики Compose
    implementation(libs.androidx.ui.tooling.preview)  // Поддержка предпросмотра в Android Studio
    implementation(libs.androidx.material3)  // Material Design 3 для Jetpack Compose
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.appcompat)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.ui.test.android) // Основная библиотека Room
    kapt(libs.androidx.room.compiler) // Компилятор Room для аннотаций
    implementation(libs.androidx.room.ktx) // KTX версия Room для удобства использования
    implementation("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.work:work-runtime-ktx:2.8.1")


    // PdfRenderer
    // https://github.com/TomRoush/PdfBox-Android
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    //  implementation("com.gemalto.jp2:jp2-android:1.0.3")

    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    // Gson
    implementation ("com.google.code.gson:gson:2.8.8")

    // Тестирование
    testImplementation(libs.junit)  // JUnit для юнит-тестирования
    androidTestImplementation(libs.androidx.junit)  // JUnit для UI тестирования
    androidTestImplementation(libs.androidx.espresso.core)  // Espresso для UI тестов
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)  // JUnit 4 для тестирования UI Compose

    // Дебаг зависимости
    debugImplementation(libs.androidx.ui.tooling)  // Предпросмотр инструментов
    debugImplementation(libs.androidx.ui.test.manifest)  // Манифест для тестирования

    // Retrofit и конвертер Gson для работы с сетью
    implementation(libs.retrofit.v2100)
    implementation(libs.converter.gson.v2100)

    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
}
kapt {
    correctErrorTypes = true
}