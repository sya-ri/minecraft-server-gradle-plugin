import dev.s7a.gradle.minecraft.server.plugin.MinecraftServerConfig

plugins {
    java
    id("dev.s7a.gradle.minecraft.server.plugin")
}

configure<MinecraftServerConfig> {
    jarUrl.set("https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.16.5.jar")
}
