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

    jarUrl.set("https://papermc.io/api/v1/paper/1.16.5/latest/download")
    jarName.set("server.jar")
    serverDirectory.set(buildDir.resolve("MinecraftPaperServer")) // build/MinecraftPaperServer
    nogui.set(true)
    copy {
        from(buildDir.resolve("libs/example.jar")) // build/libs/example.jar
        into(buildDir.resolve("MinecraftPaperServer/plugins")) // build/MinecraftPaperServer/plugins
    }
}
