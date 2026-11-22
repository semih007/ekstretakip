import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

val signingFile = providers.environmentVariable("KEYSTORE_FILE").orNull
val signingPassword = providers.environmentVariable("KEYSTORE_PASSWORD").orNull
val signingAlias = providers.environmentVariable("KEY_ALIAS").orNull
val signingKeyPassword = providers.environmentVariable("KEY_PASSWORD").orNull
val hasReleaseKey = listOf(signingFile, signingPassword, signingAlias, signingKeyPassword)
    .all { !it.isNullOrBlank() }

android {
    namespace = "com.semih007.ekstretakip"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.semih007.ekstretakip"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (hasReleaseKey) {
            create("release") {
                storeFile = rootProject.file(requireNotNull(signingFile))
                storePassword = signingPassword
                keyAlias = signingAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName(if (hasReleaseKey) "release" else "debug")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons)
    implementation(libs.activity.compose)
    testImplementation(libs.junit)
}
