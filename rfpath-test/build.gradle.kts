import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.kotest
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform") version "5.5.5"
}

description = "Testing implementations to enable testing with the path library rfpath"

configureCommon()
publishToGithub()
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":rfpath-core"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            }
        }
        val commonTest by getting {
            dependencies {
                kotest()
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
            }
        }
    }
}