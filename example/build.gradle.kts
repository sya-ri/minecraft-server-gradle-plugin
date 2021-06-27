import dev.s7a.gradle.minecraft.server.MinecraftServerConfig

plugins {
    java
    id("dev.s7a.gradle.minecraft.server")
}

configure<MinecraftServerConfig> {
    jarUrl.set("https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.16.5.jar")
}
