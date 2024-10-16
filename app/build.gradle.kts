import org.jetbrains.kotlin.konan.properties.Properties
import java.lang.System.load

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id ("io.realm.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sqli.capsulescanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sqli.capsulescanner"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (System.getenv("API_KEY") != null) {
            // Read OPENAI_API_KEY from environment variable
            val openAiApiKey = System.getenv("OPENAI_API_KEY") ?: ""
            buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
        } else {
            // Read OPENAI_API_KEY from local.properties
            val openAiApiKey: String = getApiKeyFromLocalProperties(rootDir) ?: ""
            buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")            
        }

        if (System.getenv("VISION_API_KEY") != null) {
            // Read VISION_API_KEY from environment variable
            val visionApiKey = System.getenv("VISION_API_KEY") ?: ""
            buildConfigField("String", "VISION_API_KEY", "\"$visionApiKey\"")
        } else {
            // Read VISION_API_KEY from local.properties
            val visionApiKey: String = getGoogleVisionApiKeyFromLocalProperties(rootDir) ?: ""
            buildConfigField("String", "VISION_API_KEY", "\"$visionApiKey\"")
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

    applicationVariants.all {
        outputs.all {
            val versionName = versionName
            val newFileName = "capsulescanner-${name}-${versionName}.apk"
            this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            this.outputFileName = newFileName
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
        buildConfig = true
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.media3:media3-common:1.3.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-rc01")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation ("androidx.compose.material:material-icons-extended:1.6.0-beta03")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("io.realm.kotlin:library-base:1.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.0")

    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")

    implementation ("io.coil-kt:coil-compose:2.6.0")
    implementation ("com.github.moyuruaizawa:cropify:0.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
}

kapt {
    correctErrorTypes = true
}

fun getApiKeyFromLocalProperties(rootDir: File): String? {
    val propertiesFile = File(rootDir, "local.properties")
    return if (propertiesFile.exists()) {
        val properties = Properties().apply {
            load(propertiesFile.inputStream())
        }
        properties.getProperty("OPENAI_API_KEY")
    } else {
        null
    }
}

fun getGoogleVisionApiKeyFromLocalProperties(rootDir: File): String? {
    val propertiesFile = File(rootDir, "local.properties")
    return if (propertiesFile.exists()) {
        val properties = Properties().apply {
            load(propertiesFile.inputStream())
        }
        properties.getProperty("VISION_API_KEY")
    } else {
        null
    }
}