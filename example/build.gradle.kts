import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask.JarUrl

plugins {
    java
    id("dev.s7a.gradle.minecraft.server")
}

repositories {
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
}

task<LaunchMinecraftServerTask>("launchMinecraftServer") {
    jarUrl.set(JarUrl.Paper("1.19.2"))
    // agreeEula.set(true)
    agreeEula.set(false)
}

task<LaunchMinecraftServerTask>("testPlugin") {
    dependsOn("build")

    doFirst {
        copy {
            from(buildDir.resolve("libs/example.jar"))
            into(buildDir.resolve("MinecraftServer/plugins"))
        }
    }

    jarUrl.set(JarUrl.Paper("1.19.2"))
    agreeEula.set(true)
}

listOf(
    "8" to "1.8.8",
    "9" to "1.9.4",
    "10" to "1.10.2",
    "11" to "1.11.2",
    "12" to "1.12.2",
    "13" to "1.13.2",
    "14" to "1.14.4",
    "15" to "1.15.2",
    "16" to "1.16.5",
    "17" to "1.17.1",
    "18" to "1.18.2",
    "19" to "1.19.2"
).forEach { (name, version) ->
    task<LaunchMinecraftServerTask>("testPlugin$name") {
        dependsOn("build")

        doFirst {
            copy {
                from(buildDir.resolve("libs/example.jar"))
                into(buildDir.resolve("MinecraftServer$name/plugins"))
            }
        }

        serverDirectory.set(buildDir.resolve("MinecraftServer$name"))
        jarUrl.set(JarUrl.Paper(version))
        agreeEula.set(true)
    }
}
