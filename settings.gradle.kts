pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = ("minecraft-server-gradle-plugin")

include(":example")
includeBuild("plugin-build")
