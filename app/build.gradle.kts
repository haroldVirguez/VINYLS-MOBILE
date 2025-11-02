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
        buildConfig = true
    }

    // E2E product flavors to switch BASE_URL between real backend and local mock (Express)
    flavorDimensions += "env"
    productFlavors {
        create("prod") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", "\"https://backvynils-q6yc.onrender.com/\"")
        }
        create("e2e") {
            dimension = "env"
            // http://10.0.2.2:3000/ For emulator
            // http://127.0.0.1:3000/ For physical device (after running adb reverse tcp:3000 tcp:3000 to redirect port 3000)
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/\"")
        }
    }
}

// JaCoCo coverage for unit tests
// Nota: no forzamos `useJUnitPlatform()` globalmente porque la mayoría de las pruebas unitarias
// en este módulo están escritas con JUnit4 (Android/JUnit). Forzar JUnit Platform puede
// hacer que no se ejecuten las pruebas si no está configurado el vintage engine correctamente.
// Dejar que Gradle utilice el comportamiento por defecto para las pruebas unitarias Android.

apply(plugin = "jacoco")

// Configure JaCoCo plugin extension
configure<org.gradle.testing.jacoco.plugins.JacocoPluginExtension> {
    toolVersion = "0.8.10"
}

tasks.register<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoTestReport") {
    // Depend on all unit test tasks (handles product flavors like e2e/prod)
    dependsOn(tasks.matching { it.name.matches(Regex("test.*UnitTest")) })
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
        "android/**/*.*",
        "**/*_Factory*.*",
        "**/*_Impl*.*",
        "**/*_MembersInjector*.*",
        "**/databinding/**",
        "**/*Binding*.*",
        "**/*Directions*.*",
        "**/BR.*",
        "**/Dagger*.*",
        "**/di/**",
        "**/generated/**",
        // Exclude UI package classes (these are typically validated with Robolectric or instrumentation tests)
        "**/com/team3/vinyls/albums/ui/**",
        // Exclude network adapters that are tested separately with MockWebServer or integration tests
        "**/com/team3/vinyls/core/network/**",
        // Broad exclusions for Activities and Fragments (simpler patterns)
        "**/*MainActivity*.*",
        "**/*Fragment*.*",
        // Explicitly exclude known app UI classes that should be covered by instrumentation/Robolectric, not unit tests
        "**/com/team3/vinyls/MainActivity.class",
        "**/com/team3/vinyls/FirstFragment.class",
        "**/com/team3/vinyls/SecondFragment.class",
        // Exclude Kotlin inlined/synthetic classes
        "**/*\$*inlined*.*"
    )
    // include both Kotlin-compiled classes and Java/Javac compiled classes (intermediates)
    val kotlinDebugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    val javaDebugTree = fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug/classes") { exclude(fileFilter) }
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

tasks.register<org.gradle.testing.jacoco.tasks.JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    // Depend on all unit test tasks to support flavor variants
    dependsOn(tasks.matching { it.name.matches(Regex("test.*UnitTest")) })
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*_Factory*.*",
        "**/*_Impl*.*",
        "**/*_MembersInjector*.*",
        "**/databinding/**",
        "**/*Binding*.*",
        "**/*Directions*.*",
        "**/BR.*",
        "**/Dagger*.*",
        "**/di/**",
        "**/generated/**",
        // Exclude UI package classes
        "**/com/team3/vinyls/albums/ui/**",
        // Exclude network adapters
        "**/com/team3/vinyls/core/network/**",
        // Broad exclusions for Activities and Fragments
        "**/*MainActivity*.*",
        "**/*Fragment*.*",
        // Explicitly exclude known app UI classes
        "**/com/team3/vinyls/MainActivity.class",
        "**/com/team3/vinyls/FirstFragment.class",
        "**/com/team3/vinyls/SecondFragment.class",
        // Exclude Kotlin inlined/synthetic classes
        "**/*\$*inlined*.*"
    )
    val kotlinDebugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    val javaDebugTree = fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug/classes") { exclude(fileFilter) }
    classDirectories.setFrom(files(kotlinDebugTree, javaDebugTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec", "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
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
    implementation(libs.moshi.kotlin)
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
        testImplementation(libs.test.robolectric)
        testImplementation(libs.androidx.test.core)
        testImplementation(libs.androidx.navigation.testing)
        testImplementation(libs.androidx.fragment.testing)
    testImplementation(libs.moshi.kotlin)
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
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("org.hamcrest:hamcrest:2.2")
}