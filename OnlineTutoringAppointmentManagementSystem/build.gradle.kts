// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 34 // your compile SDK
    defaultConfig {
        applicationId = "com.example.tutorstudent" // your package name
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Firebase BoM ensures compatible versions
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    // Add Firebase products you want
    implementation("com.google.firebase:firebase-analytics")    // optional
    implementation("com.google.firebase:firebase-database-ktx") // Realtime Database
    implementation("com.google.firebase:firebase-auth-ktx")     // optional Auth
    implementation("com.google.firebase:firebase-firestore-ktx") // optional Firestore
}