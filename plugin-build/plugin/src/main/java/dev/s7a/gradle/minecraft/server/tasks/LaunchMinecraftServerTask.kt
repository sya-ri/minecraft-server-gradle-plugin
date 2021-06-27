package dev.s7a.gradle.minecraft.server.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class LaunchMinecraftServerTask : MinecraftTask() {
    init {
        description = "Launch a Minecraft Server"
    }

    @get:Input
    @get:Option(option = "jarUrl", description = "To download the server jar.")
    abstract val jarUrl: Property<String>

    @get:Input
    @get:Option(option = "jarName", description = "The name of the downloaded Jar file")
    abstract val jarName: Property<String>

    @get:Input
    @get:Option(option = "serverDirectory", description = "For storing server data.")
    abstract val serverDirectory: DirectoryProperty

    @get:Input
    @get:Option(option = "jvmArgument", description = "Java arguments before .jar")
    abstract val jvmArgument: ListProperty<String>

    @get:Input
    @get:Option(option = "serverArgument", description = "Java arguments after .jar")
    abstract val serverArgument: ListProperty<String>

    @get:Input
    @get:Option(option = "nogui", description = "Start without console GUI")
    abstract val nogui: Property<Boolean>

    @TaskAction
    fun launchServer() {
        val jarUrl = jarUrl.orNull ?: error("jarUrl must not be null")
        val serverDirectory = serverDirectory.get().asFile.apply(File::mkdirs)
        val jarFile = serverDirectory.resolve(jarName.get())
        if (jarFile.exists().not()) {
            logger.lifecycle(
                """
                    Download Jar
                        url : $jarUrl
                        dest : ${jarFile.absolutePath}
                """.trimIndent()
            )
            downloadFile(jarUrl, jarFile)
        }
        project.javaexec {
            it.run {
                mainClass.set("-jar")
                jvmArgs(jvmArgument.get())
                val args = mutableListOf<String>()
                args.add(jarFile.absolutePath)
                args.addAll(serverArgument.get())
                if (nogui.get()) {
                    args.add("-nogui")
                }
                args(args)
                workingDir = serverDirectory
                logger.lifecycle(commandLine.joinToString(" "))
            }
        }
    }

    /**
     * URLからファイルをダウンロードする
     * @param url ダウンロードURL
     * @param dest 出力先
     */
    private fun downloadFile(url: String, dest: File) {
        ant.invokeMethod("get", mapOf("src" to url, "dest" to dest))
    }
}
