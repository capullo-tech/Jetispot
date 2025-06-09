buildscript {
    val version_code by extra(16)
    val version_name by extra("poc_v16")

    val compose_compiler_version by extra("1.5.8")

    val media2_version by extra("1.2.1")
    val accompanist_version by extra("0.33.0-alpha")
    val room_version by extra("2.6.1")

    val librespot_commit by extra("b7b482c")
    val hilt_version by extra("2.50")
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("com.android.library") version "8.10.1" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}