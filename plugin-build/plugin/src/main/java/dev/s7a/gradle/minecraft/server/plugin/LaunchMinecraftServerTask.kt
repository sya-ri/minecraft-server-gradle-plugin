package dev.s7a.gradle.minecraft.server.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class LaunchMinecraftServerTask : DefaultTask() {
    init {
        description = "Launch a Minecraft Server"

        // Don't forget to set the group here.
        // group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Option(option = "jarUrl", description = "To download the server jar.")
    abstract val jarUrl: Property<String>

    @TaskAction
    fun launchServer() {
        logger.lifecycle("jarUrl: ${jarUrl.orNull}")
    }
}
