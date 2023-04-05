import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform") version "5.5.5"
}

description = "Testing implementations to enable testing with the path library rfpath"

configureCommon()
publishToGithub()
kotlin {
    val okioVersion = "3.2.0"
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":rfpath-core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")

                implementation("com.soywiz.korlibs.korio:korio:3.3.1")
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-Beta")

                implementation("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }
    }
}