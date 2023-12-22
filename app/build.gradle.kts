plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tukkantuk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tukkantuk"
        minSdk = 24
        targetSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
//    implementation("org.tensorflow:tensorflow-lite-support:2.13.0")
//    implementation("org.tensorflow:tensorflow-lite-metadata:2.13.0")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.room:room-common:2.6.1")
//    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
//    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
//    implementation("com.google.firebase:firebase-auth:22.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.room:room-paging:2.4.0-rc01")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:1.4.0-alpha02")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha02")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0") // Sesuaikan dengan versi yang Anda gunakan
//    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.0")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
//    implementation(project(":opencv"))
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") //untuk lifecycleScope
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")
}