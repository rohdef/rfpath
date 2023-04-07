import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.kotest
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform") version "5.5.5"
}

description = "Path library to handle basic file system IO"

configureCommon()
publishToGithub()
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                kotest()
                implementation(project(":rfpath-test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }
    }
}