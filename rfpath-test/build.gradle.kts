import dk.rohdef.rfpath.convention.configureCommon
import dk.rohdef.rfpath.convention.kotest
import dk.rohdef.rfpath.convention.publishToGithub

plugins {
    kotlin("multiplatform")
}

description = "Testing implementations to enable testing with the path library rfpath"

configureCommon()
kotest()
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
                val kotestVersion = "5.6.2"
                implementation("io.kotest:kotest-property:$kotestVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
            }
        }
    }
}