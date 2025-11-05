import com.google.protobuf.gradle.proto

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.protobuf)
}

val submodule = rootProject.file("vendor/librespot-java")

android {
    namespace = "xyz.gianlu.librespot"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.getByName("main") {
        java.setSrcDirs(
            listOf(
                File(projectDir, "src/main/java"),
                File(submodule, "sink-api/src/main/java"),
                File(submodule, "decoder-api/src/main/java"),
                File(submodule, "lib/src/main/java"),
                File(submodule, "dacp/src/main/java"),
                File(submodule, "player/src/main/java"),
            )
        )
        resources.setSrcDirs(
            listOf(
                File(submodule, "lib/src/main/resources"),
                File(submodule, "player/src/main/resources"),
            )
        )
        proto {
            srcDir(File(submodule, "lib/src/main/proto"))
        }
        res.setSrcDirs(emptyList<File>())
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobufJava.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java")
            }
        }
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
