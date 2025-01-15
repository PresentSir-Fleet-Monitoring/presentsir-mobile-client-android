plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rdev.bstrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rdev.bstrack"
        minSdk = 23
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)  // Ensure this is the AndroidX version
    implementation(libs.material)  // Keep only this line for material components
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.android.gms:play-services-location:18.0.0")

    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")



    // OneSignal SDK with exclusion of legacy support-compat
    implementation("com.onesignal:OneSignal:[4.0.0, 4.99.99]") {
        exclude(group = "com.android.support", module = "support-compat")
    }

    // Make sure MapMyIndia SDK is AndroidX compatible
    implementation("com.mapmyindia.sdk:mapmyindia-android-sdk:7.0.3") {
        exclude(group = "com.android.support", module = "support-compat")
    }



    // Add more exclusions if needed for other libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
