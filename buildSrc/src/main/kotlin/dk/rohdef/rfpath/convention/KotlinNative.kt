package dk.rohdef.rfpath.convention

import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.configureCommon() {
    nativeTarget()

    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.5"

    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")

                    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                }
            }

            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test"))
                }
            }

            val linuxX64Main by getting {
                dependencies {
                    implementation("io.github.microutils:kotlin-logging-linuxx64:$kotlinLoggingVersion")
                }
            }

            val macosX64Main by getting {
                dependencies {
                    implementation("io.github.microutils:kotlin-logging-macosx64:$kotlinLoggingVersion")
                }
            }

            val macosArm64Main by getting {
                dependencies {
                    implementation("io.github.microutils:kotlin-logging-macosarm64:$kotlinLoggingVersion")
                }
            }

            val jvmMain by getting {
                dependencies {
                    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
                }
            }

//            val mingwX64Main by getting {
//                dependencies {
//                    implementation("io.github.microutils:kotlin-logging-mingwx64:$kotlinLoggingVersion")
//                }
//            }

            val nativeMain by getting {
                dependencies {
                    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
                }
            }
        }
    }
}

fun Project.nativeTarget() {
    apply(plugin = "kotlin-multiplatform")

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    kotlin {
        targetHierarchy.default()

        jvm()
        linuxX64()
        macosX64()
        macosArm64()
//        mingwX64()
    }
}

fun  Project.kotest() {
//    apply(plugin = "kotlin-multiplatform")

    val kotestVersion = "5.6.2"
    val arrowKtVersionKotest = "1.3.3"

    kotlin {
        jvm {
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }

        sourceSets {
            val commonTest by getting {
                dependencies {
                    implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                    implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
                    implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                    implementation("io.kotest.extensions:kotest-assertions-arrow:$arrowKtVersionKotest")
                }
            }

            val jvmTest by getting {
                dependencies {
                    implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
                }
            }
        }
    }

}