plugins {
    alias(libs.plugins.android.application)
    //id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.fireledger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fireledger"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Google 登录依赖
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Firebase 依赖
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    // Firebase Auth依赖
    implementation("com.google.firebase:firebase-auth")

    // 网络请求依赖
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    // SQLite依赖
    implementation ("androidx.sqlite:sqlite:2.3.1")

    // RecyclerView依赖
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Markdown依赖
    implementation("io.noties.markwon:core:4.6.2")

}