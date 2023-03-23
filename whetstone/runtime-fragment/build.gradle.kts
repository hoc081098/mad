plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

android {
    namespace = "com.freeletics.mad.whetstone.fragment"
    compileSdk = libs.versions.android.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
    }

    buildFeatures {
        buildConfig = false
    }

    // still needed for Android projects despite toolchain
    compileOptions {
        sourceCompatibility(JavaVersion.toVersion(libs.versions.java.target.get()))
        targetCompatibility(JavaVersion.toVersion(libs.versions.java.target.get()))
    }
}

// workaround for https://youtrack.jetbrains.com/issue/KT-37652
android.kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"

kotlin {
    explicitApi()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.target.get().toInt()))
    }

    sourceSets.all {
        languageSettings {
            optIn("com.freeletics.mad.whetstone.internal.InternalWhetstoneApi")
        }
    }
}

dependencies {
    api(projects.whetstone.runtime)
    api(libs.androidx.fragment)

    implementation(projects.stateMachine)
    implementation(projects.whetstone.scope)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.viewmodel)
    implementation(libs.androidx.viewmodel.savedstate)

    compileOnly(libs.renderer)
}