# minecraft-server-gradle-plugin

[![Pre Merge Checks](https://github.com/sya-ri/minecraft-server-gradle-plugin/workflows/Pre%20Merge%20Checks/badge.svg)](https://github.com/sya-ri/minecraft-server-gradle-plugin/actions?query=workflow%3A%22Pre+Merge+Checks%22)
[![License](https://img.shields.io/github/license/sya-ri/minecraft-server-gradle-plugin.svg)](LICENSE)
![Language](https://img.shields.io/github/languages/top/sya-ri/minecraft-server-gradle-plugin?color=blue&logo=kotlin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/dev/s7a/gradle/minecraft/server/dev.s7a.gradle.minecraft.server/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin%20Portal)](https://plugins.gradle.org/plugin/dev.s7a.gradle.minecraft.server)

Launch a Minecraft Server Using Gradle Task. For Bukkit, Spigot, Paper, etc..

- [English](README.md)
- [日本語](README.ja.md)

## Installation

### build.gradle

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server' version '1.0.0'
}
```

### build.gradle.kts

```kotlin
plugins {
    id("dev.s7a.gradle.minecraft.server") version "1.0.0"
}
```

## Options

```kotin
minecraftServerConfig {
    // Options
}

// or

configure<MinecraftServerConfig> {
    // Options
}
```

| Name | Default | Description |
|---|---|---|
| jarUrl | **Required**️ | URL to Download the .jar |
| jarName | `server.jar` | Jar File Name After Download |
| serverDirectory | `build/MinecraftServer` | Working Directory |
| jvmArgument | `[]` | [Java Options](https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html) |
| serverArgument | `[]` | [Server Options](https://www.spigotmc.org/wiki/start-up-parameters/) |
| nogui | `true` | Without Vanilla GUI |

## Example

### build.gradle

<details>
<summary><strong>Spigot 1.16.5</strong></summary>

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server' version '1.0.0'
}

minecraftServerConfig {
    jarUrl.set('https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.16.5.jar"')
}
```

</details>

<details>
<summary><strong>Paper 1.16.5</strong></summary>

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server' version '1.0.0'
}

minecraftServerConfig {
    jarUrl.set('https://papermc.io/api/v1/paper/1.16.5/latest/download')
}
```

</details>

### build.gradle.kts

<details>
<summary><strong>Spigot 1.16.5</strong></summary>

```kotlin
plugins {
    id("dev.s7a.gradle.minecraft.server") version "1.0.0"
}

minecraftServerConfig {
    jarUrl.set("https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.16.5.jar")
}
```

</details>

<details>
<summary><strong>Paper 1.16.5</strong></summary>

```kotlin
plugins {
    id("dev.s7a.gradle.minecraft.server") version "1.0.0"
}

minecraftServerConfig {
    jarUrl.set("https://papermc.io/api/v1/paper/1.16.5/latest/download")
}
```

</details>

<details>
<summary><strong>Build and Test Plugin (Paper)</strong></summary>
Create multiple server configurations by defining the tasks.

```kotlin
task<LaunchMinecraftServerTask>("buildAndLaunchServer") {
    dependsOn("jar") // build task (build, jar, shadowJar, ...)
    doFirst {
        copy {
            from(buildDir.resolve("libs/example.jar")) // build/libs/example.jar
            into(buildDir.resolve("MinecraftPaperServer/plugins")) // build/MinecraftPaperServer/plugins
        }
    }
    
    jarUrl.set("https://papermc.io/api/v1/paper/1.16.5/latest/download")
    jarName.set("server.jar")
    serverDirectory.set(buildDir.resolve("MinecraftPaperServer")) // build/MinecraftPaperServer
    nogui.set(true)
}
```

</details>

## Gradle Task

### launchMinecraftServer
Start the server.

### agreeMinecraftEULA
Agree to Minecraft EULA. Be sure to run it after `launchMinecraftServer`.

### refreshMinecraftServerJar
Delete server.jar and download it again.
