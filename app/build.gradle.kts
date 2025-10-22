plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.team3.vinyls"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.team3.vinyls"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

// JaCoCo coverage for unit tests
// Nota: no forzamos `useJUnitPlatform()` globalmente porque la mayoría de las pruebas unitarias
// en este módulo están escritas con JUnit4 (Android/JUnit). Forzar JUnit Platform puede
// hacer que no se ejecuten las pruebas si no está configurado el vintage engine correctamente.
// Dejar que Gradle utilice el comportamiento por defecto para las pruebas unitarias Android.

apply(plugin = "jacoco")

// Configure JaCoCo plugin extension using explicit type to avoid Kotlin DSL resolution issues
extensions.configure(org.gradle.testing.jacoco.plugins.JacocoPluginExtension::class.java) {
    toolVersion = "0.8.10"
}

tasks.register<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

tasks.register<org.gradle.testing.jacoco.tasks.JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("testDebugUnitTest")
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check") { dependsOn("jacocoTestCoverageVerification") }

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.recyclerview)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
        testImplementation(libs.test.coroutines)
        testImplementation(libs.test.mockwebserver)
        testImplementation(libs.test.mockito.kotlin)
        testImplementation(libs.androidx.arch.core.testing)
    testImplementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    // Robolectric for JVM UI tests (Adapter/ViewHolder)
    testImplementation("org.robolectric:robolectric:4.10")
    // AndroidX Test core for ApplicationProvider
    testImplementation("androidx.test:core:1.5.0")
    // Mockito inline to mock final Android classes (TextView, View) in unit tests
    // Use mockito-core (resolved from version catalog). If you need inline mocking of final classes,
    // add/mock the inline artifact when a compatible version is available in your repositories.
    testImplementation(libs.test.mockito.core)
    // Temporary explicit fallback to a known Mockito release on Maven Central to avoid resolution issues
    testImplementation("org.mockito:mockito-core:4.11.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}