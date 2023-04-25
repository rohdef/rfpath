import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.kotest
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform") version "5.5.5"
}

description = "Implementation of rfpath using okio (and a bit of korio)"

configureCommon()
publishToGithub()
kotlin {
    val okioVersion = "3.3.0"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":rfpath-core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("com.soywiz.korlibs.korio:korio:3.3.1")
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                kotest()
                implementation(project(":rfpath-test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")

                implementation("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }
    }
}