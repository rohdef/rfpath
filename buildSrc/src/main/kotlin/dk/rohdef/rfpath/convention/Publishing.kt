package dk.rohdef.rfpath.convention

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.util.*

fun Project.publishToGithub() {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

    val ext = this.extraProperties

    // Stub secrets to let the project sync and build without the publication values set up
    ext["signing.keyId"] = null
    ext["signing.password"] = null
    ext["signing.secretKeyRingFile"] = null
    ext["ossrhUsername"] = null
    ext["ossrhPassword"] = null

    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            Properties().apply {
                load(it)
            }
        }.onEach { (name, value) ->
            ext[name.toString()] = value
        }
    } else {
        ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
        ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
        ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    }
    fun getExtraString(name: String) = ext[name]?.toString()

    val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)
    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "sonatype"

                setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = getExtraString("ossrhUsername")
                    password = getExtraString("ossrhPassword")
                }
            }
        }

        publications.withType<MavenPublication>() {
            artifact(javadocJar)

            pom {
                name.set("rfpath")
                // TODO create description and all that jazz
                description.set("TBD")
                url.set("https://github.com/rohdef/rfpath")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("https://github.com/rohdef")
                        name.set("Rohde Fischer")
                    }
                }

                scm {
                    url.set("https://github.com/rohdef/rfpath")
                }
            }
        }

        configure<SigningExtension> {
            sign(publications)
        }
    }

    val signingTasks = tasks.withType<Sign>()
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOn(signingTasks)
    }
}