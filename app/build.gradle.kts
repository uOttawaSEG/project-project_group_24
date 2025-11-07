plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.uottawa.eecs.project_project_group_24"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.uottawa.eecs.project_project_group_24"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    // Firebase BoM ensures compatible versions
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-firestore")
    // Add Firebase products you want
    implementation("com.google.firebase:firebase-analytics")    // optional
    implementation("com.google.firebase:firebase-database") // Realtime Database
    implementation("com.google.firebase:firebase-auth")     // optional Auth
//    implementation("com.google.firebase:firebase-admin:9.7.0")
}

//apply(plugin = "com.google.gms.google-services")