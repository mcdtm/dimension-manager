pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }

  plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradleup.nmcp.settings") version "2.6.1"
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    mavenCentral()
    maven("https://jitpack.io/") {
      content {
        includeGroupAndSubgroups("com.github.kvdpxne")
      }
    }
    // https://docs.papermc.io/paper/dev/project-setup/
    //maven("https://repo.papermc.io/repository/maven-public/")
  }
}

rootProject.name = "dimension-manager"