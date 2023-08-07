plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.compose_tv_focus_test"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.compose_tv_focus_test"
        minSdk = 24
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
        freeCompilerArgs += "-opt-in=androidx.tv.material3.ExperimentalTvMaterial3Api"
        freeCompilerArgs += "-opt-in=androidx.tv.foundation.ExperimentalTvFoundationApi"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val tvCompose = "1.0.0-SNAPSHOT"
    val composeUi = "1.6.0-SNAPSHOT"
    val googleMaterial = "1.9.0"
    val activityCompose = "1.7.2"
    val lifecycleRuntime = "2.6.1"
    val navigation = "2.6.0"
    val hilt = "2.46.1"
    val hiltNavigation = "1.0.0"

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleRuntime")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleRuntime")

    implementation("androidx.activity:activity-compose:$activityCompose")
    implementation("androidx.compose.ui:ui:$composeUi")
    implementation("androidx.compose.ui:ui-graphics:$composeUi")
    implementation("androidx.compose.animation:animation-graphics:$composeUi")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUi")
    implementation("androidx.compose.foundation:foundation:$composeUi")
    implementation("com.google.android.material:material:$googleMaterial")

    // Compose for TV dependencies
    implementation("androidx.tv:tv-foundation:$tvCompose")
    implementation("androidx.tv:tv-material:$tvCompose")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:$navigation")
}