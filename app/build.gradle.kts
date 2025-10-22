plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  id("kotlin-parcelize")
  id("kotlin-kapt")
}

android {
  namespace = "com.example.playlistmaker"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.playlistmaker"
    minSdk = 29
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material.v1120)
  implementation(libs.androidx.viewpager2)
  implementation(libs.glide)
  annotationProcessor(libs.compiler)
  implementation(libs.gson)
  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.koin.android)
  implementation(libs.androidx.navigation.fragment.ktx)
  implementation(libs.androidx.navigation.ui.ktx)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  // Room
  implementation(libs.androidx.room.runtime)
  kapt(libs.androidx.room.compiler)
  implementation(libs.androidx.room.ktx)
}