import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt.android)
}

val openRouterKey: String = project.rootProject
    .file("local.properties")
    .inputStream()
    .use {
        Properties().apply { load(it) }
    }
    .getProperty("openRouterKey")?.trim() ?: ""

android {
    namespace = "com.droidconlisbon.sp2ds"
    compileSdk = 36

    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }

    defaultConfig {
        applicationId = "com.droidconlisbon.sp2ds"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPEN_ROUTER_KEY", "\"$openRouterKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildToolsVersion = "36.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.play.services.location)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.timber)
    implementation(libs.androidx.navigation.compose)
    ksp (libs.symbol.processing.api)

    //retrofit
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.okhttp)

    // Moshi
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.ksp)
    api(libs.moshi.converter)
    api(libs.moshi.adapters)

    //Hilt
    implementation(libs.hilt.android.core)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    //other
    implementation(libs.lottie.compose)
    implementation(libs.compose.markdown)
    implementation(libs.glide.core)
    implementation(libs.glide.okhttp)
    implementation(libs.glide.compose)

    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.protobuf.javalite)
    testImplementation(libs.protobuf.javalite)
    testImplementation(libs.hilt.testing)
    androidTestImplementation(libs.hilt.testing)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}