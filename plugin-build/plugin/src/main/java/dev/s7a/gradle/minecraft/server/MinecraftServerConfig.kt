package dev.s7a.gradle.minecraft.server

import org.gradle.api.Project
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
    val jarUrl = objects.property(String::class.java)

    /**
     * Server Jar Name
     */
    val jarName = objects.property(String::class.java).convention(Default.jarName)

    /**
     * Server Directory
     */
    val serverDirectory = objects.directoryProperty().convention(Default.serverDirectory(project))

    /**
     * Java Arguments Before .jar
     */
    val jvmArgument = objects.listProperty(String::class.java).convention(Default.jvmArgument)

    /**
     * Java Arguments After .jar
     */
    val serverArgument = objects.listProperty(String::class.java).convention(Default.serverArgument)

    /**
     * Without Console GUI
     */
    val nogui = objects.property(Boolean::class.java).convention(Default.nogui)
}
