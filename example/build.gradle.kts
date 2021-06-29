import dev.s7a.gradle.minecraft.server.MinecraftServerConfig
import dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask

plugins {
    java
    id("dev.s7a.gradle.minecraft.server")
}

configure<MinecraftServerConfig> {
    jarUrl.set("https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.16.5.jar")
}

task<LaunchMinecraftServerTask>("buildAndLaunchServer") {
    dependsOn("jar") // build task
    doFirst {
        copy {
            from(buildDir.resolve("libs/example.jar")) // build/libs/example.jar
            into(buildDir.resolve("MinecraftPaperServer/plugins")) // build/MinecraftPaperServer/plugins
        }
    }

    jarUrl.set("https://papermc.io/api/v1/paper/1.16.5/latest/download")
    serverDirectory.set(buildDir.resolve("MinecraftPaperServer")) // build/MinecraftPaperServer
}
