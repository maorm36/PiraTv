import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Needed for Glide annotation processing
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}


android {
    namespace = "com.example.piratv"
    compileSdk = 35

    defaultConfig {

        //load the values from apikeys.properties file
        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val apiKey = properties.getProperty("API_KEY_THEMOVIEDB") ?: ""
        val authKey = properties.getProperty("AUTH_KEY") ?: ""

        // anywhere in the code when needed use: BuildConfig.API_KEY_THEMOVIEDB
        buildConfigField(
            type = "String",
            name = "API_KEY_THEMOVIEDB",
            value = apiKey
        )

        // anywhere in the code when needed use: BuildConfig.API_KEY_THEMOVIEDB
        buildConfigField(
            type = "String",
            name = "AUTH_KEY",
            value = authKey
        )


        applicationId = "com.example.piratv"
        minSdk = 30
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // GSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lifecycle & Navigation
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Material 3 (includes Carousel via androidx.material3:material3)
    implementation(libs.androidx.material3)
    implementation(libs.material.v190)

    // Jetpack Compose (if used in any part of the app)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    // JSOUP
    implementation("org.jsoup:jsoup:1.20.1")

    // Glide (image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))

    // Firebase DB
    implementation("com.google.firebase:firebase-database-ktx")

    // Firebase analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firebase authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation(libs.androidx.annotation)

    // Firebase firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
