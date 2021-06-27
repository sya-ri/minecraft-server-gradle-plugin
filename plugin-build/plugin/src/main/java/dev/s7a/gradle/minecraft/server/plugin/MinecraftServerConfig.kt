package dev.s7a.gradle.minecraft.server.plugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class MinecraftServerConfig @Inject constructor(project: Project) {
    private val objects = project.objects

    /**
     * Server Jar Download Url
     */
    val jarUrl: Property<String> = objects.property(String::class.java)

    /**
     * Server Jar Name
     */
    val jarName: Property<String> = objects.property(String::class.java).convention("server.jar")

    /**
     * Server Directory
     */
    val serverDirectory: DirectoryProperty = objects.directoryProperty().convention(
        project.layout.buildDirectory.dir("MinecraftServer")
    )

    /**
     * Server Jar Name
     */
    val serverArgument: ListProperty<String> = objects.listProperty(String::class.java).convention(listOf())

    /**
     * Without Console GUI
     */
    val nogui: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
}
