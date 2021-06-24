package dev.s7a.gradle.minecraft.server.plugin

import dev.s7a.gradle.minecraft.server.plugin.tasks.AgreeMinecraftEULATask
import dev.s7a.gradle.minecraft.server.plugin.tasks.LaunchMinecraftServerTask
import dev.s7a.gradle.minecraft.server.plugin.tasks.RefreshMinecraftServerJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
abstract class MinecraftServerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("minecraftServerConfig", MinecraftServerConfig::class.java, project)

        project.tasks.run {
            register("launchMinecraftServer", LaunchMinecraftServerTask::class.java) {
                it.jarUrl.set(extension.jarUrl)
                it.jarName.set(extension.jarName)
                it.serverDirectory.set(extension.serverDirectory)
            }
            register("agreeMinecraftEULA", AgreeMinecraftEULATask::class.java) {
                it.serverDirectory.set(extension.serverDirectory)
            }
            register("refreshMinecraftServerJar", RefreshMinecraftServerJarTask::class.java) {
                it.jarName.set(extension.jarName)
                it.serverDirectory.set(extension.serverDirectory)
            }
        }
    }
}
