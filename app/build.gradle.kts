plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.kelimeoyunu"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kelimeoyunu"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // JSON (Gson Converter)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}