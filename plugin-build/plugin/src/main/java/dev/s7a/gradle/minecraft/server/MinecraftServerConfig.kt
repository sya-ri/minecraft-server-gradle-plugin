package dev.s7a.gradle.minecraft.server

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class MinecraftServerConfig @Inject constructor(project: Project) {
    object Default {
        const val jarName = "server.jar"
        fun serverDirectory(project: Project) = project.layout.buildDirectory.dir("MinecraftServer")
        val jvmArgument = listOf<String>()
        val serverArgument = listOf<String>()
        const val nogui = true
    }

    private val objects = project.objects

    /**
     * Server Jar Download Url
     */
    val jarUrl: Property<String> = objects.property(String::class.java)

    /**
     * Server Jar Name
     */
    val jarName: Property<String> = objects.property(String::class.java).convention(Default.jarName)

    /**
     * Server Directory
     */
    val serverDirectory: DirectoryProperty = objects.directoryProperty().convention(Default.serverDirectory(project))

    /**
     * Java Arguments Before .jar
     */
    val jvmArgument: ListProperty<String> = objects.listProperty(String::class.java).convention(Default.jvmArgument)

    /**
     * Java Arguments After .jar
     */
    val serverArgument: ListProperty<String> = objects.listProperty(String::class.java).convention(Default.serverArgument)

    /**
     * Without Console GUI
     */
    val nogui: Property<Boolean> = objects.property(Boolean::class.java).convention(Default.nogui)
}
