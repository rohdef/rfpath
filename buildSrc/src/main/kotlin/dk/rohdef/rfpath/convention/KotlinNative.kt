package dk.rohdef.rfpath.convention

import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.configureCommon() {
    nativeTarget()

    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.3"

    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")

                    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                    implementation("io.github.microutils:kotlin-logging-linuxx64:$kotlinLoggingVersion")
                }
            }

            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}

fun KotlinDependencyHandler.kotest() {
    val kotestVersion = "5.5.5"
    val arrowKtVersionKotest = "1.3.0"

    implementation("io.kotest:kotest-assertions-core:$kotestVersion")
    implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    implementation("io.kotest:kotest-framework-engine:$kotestVersion")
    implementation("io.kotest.extensions:kotest-assertions-arrow:$arrowKtVersionKotest")
}

fun Project.nativeTarget() {
    apply(plugin = "kotlin-multiplatform")

    kotlin {
        val hostOs = System.getProperty("os.name")
        val isMingwX64 = hostOs.startsWith("Windows")
        val nativeTarget = when {
            hostOs == "Mac OS X" -> macosX64("native")
            hostOs == "Linux" -> linuxX64("native")
            isMingwX64 -> mingwX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }
    }
}