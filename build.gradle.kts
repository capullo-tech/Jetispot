buildscript {
    val version_code by extra(15)
    val version_name by extra("poc_v15")

    val compose_version by extra("1.5.0")
    val compose_m3_version by extra("1.1.1")
    val compose_compiler_version by extra("1.5.1")

    val media2_version by extra("1.2.1")
    val accompanist_version by extra("0.33.0-alpha")
    val room_version by extra("2.5.2")

    val librespot_commit by extra("efe5b3398e")
    val hilt_version by extra("2.47")
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}