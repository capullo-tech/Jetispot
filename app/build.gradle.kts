import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.moshix)
}

val versionMajor = 0
val versionMinor = 1
val versionPatch = 6
val versionBuild = 0
val isStable = true

val keystorePropertiesFile: File = rootProject.file("keystore.properties")

val splitApks = !project.hasProperty("noSplits")

android {
    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))

        signingConfigs {
            getByName("debug") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    compileSdk = 34

    defaultConfig {
        applicationId = "bruhcollective.itaysonlab.jetispot"

        minSdk = 23
        targetSdk = 34
        versionCode = 1000
        versionName = StringBuilder("${versionMajor}.${versionMinor}.${versionPatch}").apply {
            if (!isStable) append("-beta.${versionBuild}")
        }.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["disableAnalytics"] = "true"

        if (!splitApks) {
            ndk {
                (properties["ABI_FILTERS"] as String).split(';').forEach {
                    abiFilters.add(it)
                }
            }
        }
    }

    if (splitApks) {
        splits {
            abi {
                isEnable = !project.hasProperty("noSplits")
                reset()
                include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                isUniversalApk = false
            }
        }
    }

    //source sets in .kts
    sourceSets {
        getByName("main") {
            java.srcDir("src/main/libs")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("release")
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
    }

    compileOptions {
        // coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Jetispot-${defaultConfig.versionName}-${name}.apk"
        }
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
    }

    packaging {
        resources {
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/*.version"
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/kotlinx/**"
            excludes += "**/*.properties"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin-tooling-metadata.json"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "log4j2.xml"
            excludes += "**.proto"
        }
    }
    namespace = "bruhcollective.itaysonlab.jetispot"
}

moshi {
    // Opt-in to enable moshi-sealed, disabled by default.
    enableSealed.set(true)
}

ksp {
    arg(RoomSchemaArgProvider(project.file("schemas")))
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    // AndroidX
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.customview)
    debugImplementation(libs.androidx.customview.poolingcontainer)

    // Compose - Additions
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.systemuicontroller)
    implementation("io.github.fornewid:material-motion-compose-core:1.0.6")
    implementation("io.github.fornewid:material-motion-compose-navigation:1.0.6")

    // Images
    implementation(libs.coil.compose)

    // DI
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Playback
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.slf4j.handroid)
    implementation(libs.androidx.media2.session)
    implementation(libs.androidx.media2.player)

    // Librespot
    implementation("com.github.gsalinaslopez.librespot-java:librespot-player:58350e2:thin") {
        exclude(group = "xyz.gianlu.librespot", module = "librespot-sink")
        exclude(group = "com.lmax", module = "disruptor")
        exclude(group = "org.apache.logging.log4j")
    }

    // Data - Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-protobuf:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Data - SQL
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Data - Proto
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-java:3.25.1")
    implementation("com.tencent:mmkv:1.3.2")
}

//https://stackoverflow.com/questions/65390807/unresolved-reference-protoc-when-using-gradle-protocol-buffers
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.5"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    //option("lite")
                }
            }
        }
    }
}

class RoomSchemaArgProvider(schemaDirectory: File) : CommandLineArgumentProvider {

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File = schemaDirectory

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}