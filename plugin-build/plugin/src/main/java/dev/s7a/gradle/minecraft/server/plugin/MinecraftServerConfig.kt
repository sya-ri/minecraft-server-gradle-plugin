package dev.s7a.gradle.minecraft.server.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class MinecraftServerConfig @Inject constructor(project: Project) {
    private val objects = project.objects

    /**
     * Server Jar; Bukkit, Spigot, Paper, ...
     */
    val jarUrl: Property<String> = objects.property(String::class.java)
}
