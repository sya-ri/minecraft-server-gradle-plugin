pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = ("minecraft-server-gradle-plugin")

include(":example")
includeBuild("plugin-build")
