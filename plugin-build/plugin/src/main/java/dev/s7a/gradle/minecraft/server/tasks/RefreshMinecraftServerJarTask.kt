package dev.s7a.gradle.minecraft.server.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class RefreshMinecraftServerJarTask : DefaultTask() {
    init {
        group = "minecraft"
        description = "Delete Server Jar File"
    }

    @get:Input
    @get:Option(option = "jarName", description = "The name of the downloaded Jar file")
    abstract val jarName: Property<String>

    @get:Input
    @get:Option(option = "serverDirectory", description = "For storing server data.")
    abstract val serverDirectory: DirectoryProperty

    @TaskAction
    fun refreshAction() {
        val serverDirectory = serverDirectory.get().asFile.apply(File::mkdirs)
        val jarFile = serverDirectory.resolve(jarName.get())
        jarFile.delete()
    }
}
