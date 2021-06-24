package dev.s7a.gradle.minecraft.server.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "minecraftServerConfig"
const val TASK_NAME = "launchMinecraftServer"

@Suppress("unused")
abstract class MinecraftServerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, MinecraftServerConfig::class.java, project)

        project.tasks.register(TASK_NAME, LaunchMinecraftServerTask::class.java) {
            it.jarUrl.set(extension.jarUrl)
        }
    }
}
