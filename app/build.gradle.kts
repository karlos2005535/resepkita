plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.resepkita"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.resepkita"
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
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("com.squareup.picasso:picasso:2.71828")// Or the latest version


    // Tambahkan Picasso untuk image loading
    implementation("com.squareup.picasso:picasso:2.71828")

    testImplementation(libs.junit)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}