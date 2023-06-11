plugins {
    `kotlin-dsl`

    // don't add the Kotlin JVM plugin
    // kotlin("jvm") version embeddedKotlinVersion
    // Why? It's a long story, but Gradle uses an embedded version of Kotlin,
    // (which is provided by the `kotlin-dsl` plugin)
    // which means importing an external version _might_ cause issues
    // It's annoying but not important. The Kotlin plugin version below,
    // in dependencies { }, will be used for building our 'main' project.
    // https://github.com/gradle/gradle/issues/16345
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "1.8.21"
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.20")
    implementation("io.kotest:kotest-framework-multiplatform-plugin-gradle:5.6.2")
}