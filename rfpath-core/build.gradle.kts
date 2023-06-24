import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.kotest
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
}

description = "Path library to handle basic file system IO"

configureCommon()
kotest()
publishToGithub()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

                // Needed for UUID
                implementation("com.soywiz.korlibs.korio:korio:4.0.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":rfpath-test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
            }
        }
    }
}