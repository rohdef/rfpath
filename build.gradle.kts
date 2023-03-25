allprojects {
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()

        maven {
            name = "GitHubPackages"
            url = java.net.URI.create("https://maven.pkg.github.com/rohdef/rfpath")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

subprojects {
}