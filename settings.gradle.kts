pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            name = "OSS-Sonatype"
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = uri("https://jitpack.io")
            name = "OSS-Sonatype"
        }
    }
}

rootProject.name = "SharedPreferences to DataStore"
include(":app")
 