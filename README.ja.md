# minecraft-server-gradle-plugin

[![Pre Merge Checks](https://github.com/sya-ri/minecraft-server-gradle-plugin/workflows/Pre%20Merge%20Checks/badge.svg)](https://github.com/sya-ri/minecraft-server-gradle-plugin/actions?query=workflow%3A%22Pre+Merge+Checks%22)  [![License](https://img.shields.io/github/license/sya-ri/minecraft-server-gradle-plugin.svg)](LICENSE) ![Language](https://img.shields.io/github/languages/top/sya-ri/minecraft-server-gradle-plugin?color=blue&logo=kotlin)

Gradle タスクを使って、Minecraft サーバーを起動するためのプラグイン。Bukkit, Spigot, Paper 等に対応しています。

- [English](README.md)
- [日本語](README.ja.md)

## 導入方法

### build.gradle

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server.plugin' version '1.0.0'
}
```

### build.gradle.kts

```kotlin
plugins {
    id("dev.s7a.gradle.minecraft.server.plugin") version "1.0.0"
}
```

## オプション

```kotlin
minecraftServerConfig {
    // ここに設定を記述
}

// もしくは

configure<MinecraftServerConfig> {
    // ここに設定を記述
}
```

| 名前 | デフォルト | 説明 |
|---|---|---|
| jarUrl | **必須**️ | .jar をダウンロードするための URL |
| jarName | `server.jar` | ダウンロード後のファイル名 |
| serverDirectory | `build/MinecraftServer` | 作業ディレクトリ |
| jvmArgument | `[]` | [Java のオプション](https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html) |
| serverArgument | `[]` | [Server のオプション](https://www.spigotmc.org/wiki/start-up-parameters/) |
| nogui | `true` | バニラのGUIを使用しない |

## Example

### build.gradle

<details>
<summary><strong>Spigot 1.16.5</strong></summary>

```groovy
plugins {
    id 'dev.s7a.gradle.minecraft.server.plugin' version '1.0.0'
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
    id 'dev.s7a.gradle.minecraft.server.plugin' version '1.0.0'
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
    id("dev.s7a.gradle.minecraft.server.plugin") version "1.0.0"
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
    id("dev.s7a.gradle.minecraft.server.plugin") version "1.0.0"
}

minecraftServerConfig {
    jarUrl.set("https://papermc.io/api/v1/paper/1.16.5/latest/download")
}
```

</details>