package dk.rohdef.rfpath.convention

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.*
import java.net.URI

fun Project.publishToGithub() {
    apply(plugin = "maven-publish")

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = URI.create("https://maven.pkg.github.com/rohdef/rfpath")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }
}