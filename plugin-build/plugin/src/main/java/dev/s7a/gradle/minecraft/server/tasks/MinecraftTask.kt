package dev.s7a.gradle.minecraft.server.tasks

import org.gradle.api.DefaultTask

abstract class MinecraftTask : DefaultTask() {
    init {
        group = "minecraft"
    }
}
