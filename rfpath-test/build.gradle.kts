plugins {
    val kotlinVersion = "1.8.20-RC2"
    kotlin("multiplatform") version kotlinVersion

    id("io.kotest.multiplatform") version "5.5.5"
}

group = "dk.rohdef.rfpath.rfpath-test"
description = "Testing implementations to enable testing with the path library rfpath"

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    val kotestVersion = "5.5.5"
    val okioVersion = "3.2.0"
    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.3"
    val arrowKtVersionKotest = "1.3.0"
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":rfpath"))
                implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")

                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                implementation("io.github.microutils:kotlin-logging-linuxx64:$kotlinLoggingVersion")

                implementation("com.soywiz.korlibs.korio:korio:3.3.1")
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-Beta")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest.extensions:kotest-assertions-arrow:$arrowKtVersionKotest")

                implementation("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }
    }
}