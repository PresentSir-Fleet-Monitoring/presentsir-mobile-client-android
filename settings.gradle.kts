pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.mapmyindia.com/repository/mapmyindia/") // Add MapmyIndia repository
        maven("https://jitpack.io") // Add MapmyIndia repository
    }
}

rootProject.name = "BsTrack"
include(":app")
