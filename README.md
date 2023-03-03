# minecraft-server-gradle-plugin

[![Pre Merge Checks](https://github.com/sya-ri/minecraft-server-gradle-plugin/workflows/Pre%20Merge%20Checks/badge.svg)](https://github.com/sya-ri/minecraft-server-gradle-plugin/actions?query=workflow%3A%22Pre+Merge+Checks%22)
[![License](https://img.shields.io/github/license/sya-ri/minecraft-server-gradle-plugin.svg)](LICENSE)
![Language](https://img.shields.io/github/languages/top/sya-ri/minecraft-server-gradle-plugin?color=blue&logo=kotlin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/dev/s7a/gradle/minecraft/server/dev.s7a.gradle.minecraft.server/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/dev.s7a.gradle.minecraft.server)

Launch Minecraft servers using Gradle task. For Bukkit, Spigot, Paper, etc..

## Installation

### build.gradle

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server' version '2.0.0'
}
```

### build.gradle.kts

```kotlin
plugins {
    id("dev.s7a.gradle.minecraft.server") version "2.0.0"
}
```

## Options

| Name            | Default                 | Description                                                                             |
|-----------------|-------------------------|-----------------------------------------------------------------------------------------|
| jarUrl          | **Required**️           | URL to Download the .jar                                                                |
| jarName         | `server.jar`            | Jar File Name After Download                                                            |
| serverDirectory | `build/MinecraftServer` | Working Directory                                                                       |
| jvmArgument     | `[]`                    | [Java Options](https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html) |
| serverArgument  | `[]`                    | [Server Options](https://www.spigotmc.org/wiki/start-up-parameters/)                    |
| nogui           | `true`                  | Without Vanilla GUI                                                                     |
| agreeEula       | `false`                 | Agree to the Minecraft EULA                                                             |

## Example

> **Warning**
> This plugin doesn't have a default task. So you have to define it yourself.

### Simple usage

#### build.gradle.kts

```kotlin
task<LaunchMinecraftServerTask>("launchMinecraftServer") {
    jarUrl.set(JarUrl.Paper("1.19.2"))
    agreeEula.set(true)
}
```

### For testing a plugin

#### build.gradle.kts

```kotlin
task<LaunchMinecraftServerTask>("testPlugin") {
    dependsOn("build")

    doFirst {
        copy {
            from(buildDir.resolve("libs/${project.name}.jar"))
            into(buildDir.resolve("MinecraftServer/plugins"))
        }
    }

    jarUrl.set(JarUrl.Paper("1.19.2"))
    agreeEula.set(true)
}
```

### For testing a multi-version supporting plugin

#### build.gradle.kts

```kotlin
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
    "19" to "1.19.3"
).forEach { (name, version) ->
    task<LaunchMinecraftServerTask>("testPlugin$name") {
        dependsOn("build")

        doFirst {
            copy {
                from(buildDir.resolve("libs/${project.name}.jar"))
                into(buildDir.resolve("MinecraftServer$name/plugins"))
            }
        }

        serverDirectory.set(buildDir.resolve("MinecraftServer$name"))
        jarUrl.set(JarUrl.Paper(version))
        agreeEula.set(true)
    }
}
```
