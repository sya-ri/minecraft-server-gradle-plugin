package dev.s7a.gradle.minecraft.server.tasks

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.net.URL

abstract class LaunchMinecraftServerTask : DefaultTask() {
    init {
        group = "minecraft"
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
        get() = jarName.orElse("server.jar")

    @get:Input
    @get:Optional
    @get:Option(option = "serverDirectory", description = "For storing server data.")
    abstract val serverDirectory: DirectoryProperty

    /**
     * @see serverDirectory
     */
    private val serverDirectoryOrDefault
        get() = serverDirectory.orElse(project.layout.buildDirectory.dir("MinecraftServer"))

    @get:Input
    @get:Optional
    @get:Option(option = "jvmArgument", description = "Java arguments before .jar")
    abstract val jvmArgument: ListProperty<String>

    /**
     * @see jvmArgument
     */
    private val jvmArgumentOrDefault
        get() = jvmArgument.orElse(listOf())

    @get:Input
    @get:Optional
    @get:Option(option = "serverArgument", description = "Java arguments after .jar")
    abstract val serverArgument: ListProperty<String>

    /**
     * @see serverArgument
     */
    private val serverArgumentOrDefault
        get() = serverArgument.orElse(listOf())

    @get:Input
    @get:Optional
    @get:Option(option = "nogui", description = "Start without console GUI")
    abstract val nogui: Property<Boolean>

    /**
     * @see nogui
     */
    private val noguiOrDefault
        get() = nogui.orElse(true)

    @get:Input
    @get:Optional
    @get:Option(option = "agreeEula", description = "Agree to the Minecraft EULA")
    abstract val agreeEula: Property<Boolean>

    /**
     * @see agreeEula
     */
    private val agreeEulaOrDefault
        get() = agreeEula.orElse(false)

    @TaskAction
    fun launchServer() {
        val jarUrl = jarUrl.get()
        val serverDirectory = serverDirectoryOrDefault.get().asFile.apply(File::mkdirs)
        val jarName = jarNameOrDefault.get()
        val jarFile = serverDirectory.resolve(jarName)
        val jarVersionFile = serverDirectory.resolve("$jarName.txt")
        val downloadMessage = when {
            jarFile.exists().not() -> ""
            jarVersionFile.exists().not() || jarVersionFile.readText() != jarUrl -> "(Auto-Refresh)"
            else -> null
        }
        if (downloadMessage != null) {
            logger.lifecycle(
                """
                    Download Jar $downloadMessage
                        url : $jarUrl
                        dest : ${jarFile.absolutePath}
                """.trimIndent()
            )
            downloadFile(jarUrl, jarFile)
            jarVersionFile.writeText(jarUrl)
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
                    args.add("nogui")
                }
                args(args)
                workingDir = serverDirectory
                standardInput = System.`in`
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

    /**
     * @see jarUrl
     */
    object JarUrl {
        private val json = Json { ignoreUnknownKeys = true }
        private const val fabricApiUrl = "https://meta.fabricmc.net/v2"

        @Serializable
        private data class Version(val builds: List<Int>)

        @Serializable
        private data class FabricInstallerVersion(
            val url: String,
            val maven: String,
            val version: String,
            val stable: Boolean
        )

        @Serializable
        private data class FabricLoaderVersion(
            val separator: String,
            val maven: String,
            val version: String,
            val stable: Boolean
        )

        /**
         * Using [Paper](https://papermc.io) as [jarUrl].
         *
         * ```
         * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper("1.17.1"))
         * ```
         *
         * @param version [Paper version](https://papermc.io/api/v2/projects/paper)
         * @return URL
         */
        @Suppress("FunctionName")
        fun Paper(version: String): String {
            val versionsUrl = "https://papermc.io/api/v2/projects/paper/versions"
            val versionsJson = URL("$versionsUrl/$version").readText()
            val build = json.decodeFromString<Version>(versionsJson).builds.maxOrNull()
            return "$versionsUrl/$version/builds/$build/downloads/paper-$version-$build.jar"
        }

        /**
         * Using [Velocity](https://papermc.io) as [jarUrl].
         *
         * ```
         * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Velocity("3.1.2-SNAPSHOT"))
         * ```
         *
         * @param version [Velocity version](https://papermc.io/api/v2/projects/velocity).
         * @return URL
         */
        @Suppress("FunctionName")
        fun Velocity(version: String): String {
            val versionsUrl = "https://papermc.io/api/v2/projects/velocity/versions"
            val versionsJson = URL("$versionsUrl/$version").readText()
            val build = json.decodeFromString<Version>(versionsJson).builds.maxOrNull()
            return "$versionsUrl/$version/builds/$build/downloads/velocity-$version-$build.jar"
        }

        /**
         * Using [Waterfall](https://papermc.io) as [jarUrl].
         *
         * ```
         * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Waterfall("1.19"))
         * ```
         *
         * @param version [Waterfall version](https://papermc.io/api/v2/projects/waterfall).
         * @return URL
         */
        @Suppress("FunctionName")
        fun Waterfall(version: String): String {
            val versionsUrl = "https://papermc.io/api/v2/projects/waterfall/versions"
            val versionsJson = URL("$versionsUrl/$version").readText()
            val build = json.decodeFromString<Version>(versionsJson).builds.maxOrNull()
            return "$versionsUrl/$version/builds/$build/downloads/waterfall-$version-$build.jar"
        }

        /**
         * Using [Fabric](https://fabricmc.net) as [jarUrl].
         *
         * ```
         * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Fabric("1.19.2", "0.14.11"))
         * ```
         *
         * @param minecraftVersion [Minecraft version](https://meta.fabricmc.net/v2/versions/game).
         * @param loaderVersion [Fabric Loader version](https://meta.fabricmc.net/v2/versions/loader)
         * @return URL
         */
        @Suppress("FunctionName")
        fun Fabric(minecraftVersion: String, loaderVersion: String): String {
            val installerVersionsUrl = "$fabricApiUrl/versions/installer"
            val installerVersionsJson = URL(installerVersionsUrl).readText()
            val latestInstallerVersion =
                json.decodeFromString<List<FabricInstallerVersion>>(installerVersionsJson).filter { it.stable }
                    .sortedWith(
                        compareBy(
                            { it.version.split(".")[0] },
                            { it.version.split(".")[1] },
                            { it.version.split(".")[2] }
                        )
                    ).asReversed()[0].version

            return "$fabricApiUrl/versions/loader/$minecraftVersion/$loaderVersion/$latestInstallerVersion/server/jar"
        }

        /**
         * Using [Fabric](https://fabricmc.net) as [jarUrl].
         *
         * ```
         * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Fabric("1.19.2"))
         * ```
         *
         * @param minecraftVersion [Minecraft version](https://meta.fabricmc.net/v2/versions/game).
         * @return URL
         */
        @Suppress("FunctionName")
        fun Fabric(minecraftVersion: String): String {
            val loaderVersionsUrl = "$fabricApiUrl/versions/loader"
            val loaderVersionsJson = URL(loaderVersionsUrl).readText()
            val latestLoaderVersion =
                json.decodeFromString<List<FabricLoaderVersion>>(loaderVersionsJson).filter { it.stable }.sortedWith(
                    compareBy(
                        { it.version.split(it.separator)[0] },
                        { it.version.split(it.separator)[1] },
                        { it.version.split(it.separator)[2] }
                    )
                ).asReversed()[0].version
            return Fabric(minecraftVersion, latestLoaderVersion)
        }
    }
}
