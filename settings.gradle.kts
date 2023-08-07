import java.net.URI

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI.create("https://androidx.dev/snapshots/builds/10327867/artifacts/repository") }
    }
}

rootProject.name = "ComposeTVFocusTest"
include(":app")
