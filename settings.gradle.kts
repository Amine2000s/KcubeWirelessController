pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        flatDir {
            dirs("libs")
        }


       // maven { url =uri("https://jitpack.io") }


    }
}
dependencyResolutionManagement {
   // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("libs")
        }

    }
}

rootProject.name = "KcubeWirelessController"
include(":app")
 