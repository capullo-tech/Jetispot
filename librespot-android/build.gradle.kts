import com.google.protobuf.gradle.proto

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "xyz.gianlu.librespot"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobufJava.get()}"
    }
    generateProtoTasks {
        all().forEach { task -> task.builtins { create("java") } }
    }
}

dependencies {
    api(libs.protobuf.java)
    api(libs.slf4j.handroid)

    api("com.google.code.gson:gson:2.11.0")
    api("org.jcraft:jorbis:0.0.17")
    api("com.badlogicgames.jlayer:jlayer:1.0.2-gdx")
    api("xyz.gianlu.zeroconf:zeroconf:1.3.2")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("commons-net:commons-net:3.11.1")
    api("com.electronwill.night-config:toml:3.6.7")

    compileOnly("org.apache.logging.log4j:log4j-api:2.24.3")
    compileOnly("org.apache.logging.log4j:log4j-core:2.24.3")
}