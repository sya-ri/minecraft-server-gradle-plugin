package dev.s7a.gradle.minecraft.server.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import dev.s7a.gradle.minecraft.server.MinecraftServerConfig.Default as DefaultConfig

abstract class LaunchMinecraftServerTask : MinecraftTask() {
    init {
        description = "Launch a Minecraft Server"
    }

    @get:Input
    @get:Option(option = "jarUrl", description = "To download the server jar.")
    abstract val jarUrl: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "jarName", description = "The name of the downloaded Jar file")
    abstract val jarName: Property<String>

    /**
     * @see jarName
     */
    private val jarNameOrDefault
        get() = jarName.orElse(DefaultConfig.jarName)

    @get:Input
    @get:Optional
    @get:Option(option = "serverDirectory", description = "For storing server data.")
    abstract val serverDirectory: DirectoryProperty

    /**
     * @see serverDirectory
     */
    private val serverDirectoryOrDefault
        get() = serverDirectory.orElse(DefaultConfig.serverDirectory(project))

    @get:Input
    @get:Optional
    @get:Option(option = "jvmArgument", description = "Java arguments before .jar")
    abstract val jvmArgument: ListProperty<String>

    /**
     * @see jvmArgument
     */
    private val jvmArgumentOrDefault
        get() = jvmArgument.orElse(DefaultConfig.jvmArgument)

    @get:Input
    @get:Optional
    @get:Option(option = "serverArgument", description = "Java arguments after .jar")
    abstract val serverArgument: ListProperty<String>

    /**
     * @see serverArgument
     */
    private val serverArgumentOrDefault
        get() = serverArgument.orElse(DefaultConfig.serverArgument)

    @get:Input
    @get:Optional
    @get:Option(option = "nogui", description = "Start without console GUI")
    abstract val nogui: Property<Boolean>

    /**
     * @see nogui
     */
    private val noguiOrDefault
        get() = nogui.orElse(DefaultConfig.nogui)

    @get:Input
    @get:Optional
    @get:Option(option = "agreeEula", description = "Agree to the Minecraft EULA")
    abstract val agreeEula: Property<Boolean>

    /**
     * @see agreeEula
     */
    private val agreeEulaOrDefault
        get() = agreeEula.orElse(DefaultConfig.agreeEula)

    @TaskAction
    fun launchServer() {
        val jarUrl = jarUrl.get()
        val serverDirectory = serverDirectoryOrDefault.get().asFile.apply(File::mkdirs)
        val jarFile = serverDirectory.resolve(jarNameOrDefault.get())
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
        if (agreeEulaOrDefault.get()) {
            val eulaFile = serverDirectory.resolve("eula.txt")
            if (eulaFile.exists().not() || eulaFile.readText().contains("eula=true").not()) {
                eulaFile.writeText("eula=true")
            }
        }
        project.javaexec {
            it.run {
                mainClass.set("-jar")
                jvmArgs(jvmArgumentOrDefault.get())
                val args = mutableListOf<String>()
                args.add(jarFile.absolutePath)
                args.addAll(serverArgumentOrDefault.get())
                if (noguiOrDefault.get()) {
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
