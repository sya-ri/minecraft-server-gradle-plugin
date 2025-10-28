package dev.s7a.gradle.minecraft.server.tasks

import dev.s7a.gradle.minecraft.server.exception.NotFoundMohistBuildException
import dev.s7a.gradle.minecraft.server.exception.NotFoundPaperBuildException
import dev.s7a.gradle.minecraft.server.exception.NotFoundVersionException
import dev.s7a.gradle.minecraft.server.exception.UnsupportedProtocolException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.Path

/**
 * ```kotlin
 * task<LaunchMinecraftServerTask>("launchServer") {
 *     jarUrl.set(JarUrl.Paper("1.19.2"))
 *     agreeEula.set(true)
 * }
 * ```
 */
@Suppress("unused")
abstract class LaunchMinecraftServerTask : DefaultTask() {
    init {
        group = "minecraft"
        description = "Launch a Minecraft Server"
    }

    @get:Input
    @get:Option(option = "jarUrl", description = "To download the server jar.")
    abstract val jarUrl: Property<JarUrl>

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
    abstract val serverDirectory: Property<String>

    /**
     * @see serverDirectory
     */
    private val serverDirectoryOrDefault
        get() =
            serverDirectory.orElse(
                project.layout.buildDirectory
                    .dir("MinecraftServer")
                    .get()
                    .asFile.absolutePath,
            )

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
        val jarUrl = jarUrl.get().get()
        val serverDirectory = File(serverDirectoryOrDefault.get()).apply(File::mkdirs)
        val jarName = jarNameOrDefault.get()
        val jarFile = serverDirectory.resolve(jarName)
        val jarVersionFile = serverDirectory.resolve("$jarName.txt")
        val downloadMessage =
            when {
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
                """.trimIndent(),
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
     * Download server jar from [url]
     *
     * @param url URL
     * @param dest Destination
     */
    private fun downloadFile(
        url: String,
        dest: File,
    ) {
        when (val protocol = url.substringBefore("://")) {
            "http", "https" -> ant.invokeMethod("get", mapOf("src" to url, "dest" to dest))
            "file" -> File(url.substring("file://".length)).copyTo(dest)
            else -> throw UnsupportedProtocolException(protocol)
        }
    }

    /**
     * @see jarUrl
     */
    fun interface JarUrl {
        fun get(): String

        companion object {
            private val json = Json { ignoreUnknownKeys = true }

            @Serializable
            private data class PaperProject(
                val versions: Map<String, List<String>>,
            )

            @Serializable
            private data class PaperProjectVersion(
                val builds: List<Int>,
            )

            @Serializable
            private data class PaperProjectBuild(
                val downloads: Map<String, Download>,
            ) {
                @Serializable
                data class Download(
                    val url: String,
                )
            }

            @Serializable
            private data class FabricInstallerVersion(
                val url: String,
                val maven: String,
                val version: String,
                val stable: Boolean,
            )

            @Serializable
            private data class FabricLoaderVersion(
                val separator: String,
                val maven: String,
                val version: String,
                val stable: Boolean,
            )

            @Serializable
            private data class MohistBuilds(
                val projectName: String,
                val projectVersion: String,
                val builds: List<Build>,
            ) {
                @Serializable
                data class Build(
                    val number: Int,
                    val gitSha: String,
                    val forgeVersion: String,
                    val fileMd5: String,
                    val originUrl: String,
                    val url: String,
                    val createdAt: Long,
                )
            }

            @Suppress("FunctionName")
            private fun FromPaperProject(
                projectName: String,
                version: String,
                type: String,
            ): JarUrl =
                JarUrl {
                    val projectUrl = "https://fill.papermc.io/v3/projects/$projectName"
                    val versionUrl = "$projectUrl/versions/$version"
                    val versionsJson =
                        try {
                            URL(versionUrl).readText()
                        } catch (
                            @Suppress("SwallowedException") err: FileNotFoundException,
                        ) {
                            val versions = json.decodeFromString<PaperProject>(URL(projectUrl).readText()).versions.flatMap { it.value }
                            throw NotFoundVersionException(version, versions)
                        }
                    val buildNumber = json.decodeFromString<PaperProjectVersion>(versionsJson).builds.maxOrNull()
                    val buildUrl = "$versionUrl/builds/$buildNumber"
                    val build = json.decodeFromString<PaperProjectBuild>(URL(buildUrl).readText())
                    build.downloads[type]?.url ?: throw NotFoundPaperBuildException(version, type)
                }

            /**
             * Using [Paper](https://papermc.io) as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Paper("1.17.1"))
             * ```
             *
             * @param version [Paper version](https://fill.papermc.io/v3/projects/paper)
             * @return URL
             */
            @Suppress("FunctionName")
            fun Paper(
                version: String,
                type: String = "server:default",
            ): JarUrl = FromPaperProject("paper", version, type)

            /**
             * Using [Folia](https://papermc.io) as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Folia("1.20.4"))
             * ```
             *
             * @param version [Folia version](https://fill.papermc.io/v3/projects/folia)
             * @return URL
             */
            @Suppress("FunctionName")
            fun Folia(
                version: String,
                type: String = "server:default",
            ): JarUrl = FromPaperProject("folia", version, type)

            /**
             * Using [Velocity](https://papermc.io) as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Velocity("3.1.2-SNAPSHOT"))
             * ```
             *
             * @param version [Velocity version](https://fill.papermc.io/v3/projects/velocity).
             * @return URL
             */
            @Suppress("FunctionName")
            fun Velocity(
                version: String,
                type: String = "server:default",
            ): JarUrl = FromPaperProject("velocity", version, type)

            /**
             * Using [Waterfall](https://papermc.io) as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Waterfall("1.19"))
             * ```
             *
             * @param version [Waterfall version](https://fill.papermc.io/v3/projects/waterfall).
             * @return URL
             */
            @Suppress("FunctionName")
            @Deprecated("No longer maintained. For more information, see the official announcement.")
            fun Waterfall(
                version: String,
                type: String = "server:default",
            ): JarUrl = FromPaperProject("waterfall", version, type)

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
            fun Fabric(
                minecraftVersion: String,
                loaderVersion: String,
            ): JarUrl =
                JarUrl {
                    val loaderVersionsUrl = "https://meta.fabricmc.net/v2/versions/loader"
                    val installerVersionsUrl = "https://meta.fabricmc.net/v2/versions/installer"
                    val installerVersionsJson = URL(installerVersionsUrl).readText()
                    val latestInstallerVersion =
                        json
                            .decodeFromString<List<FabricInstallerVersion>>(installerVersionsJson)
                            .filter { it.stable }
                            .sortedWith(
                                compareBy(
                                    { it.version.split(".")[0] },
                                    { it.version.split(".")[1] },
                                    { it.version.split(".")[2] },
                                ),
                            ).asReversed()[0]
                            .version

                    "$loaderVersionsUrl/$minecraftVersion/$loaderVersion/$latestInstallerVersion/server/jar"
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
            fun Fabric(minecraftVersion: String): JarUrl =
                JarUrl {
                    val loaderVersionsUrl = "https://meta.fabricmc.net/v2/versions/loader"
                    val loaderVersionsJson = URL(loaderVersionsUrl).readText()
                    val latestLoaderVersion =
                        json
                            .decodeFromString<List<FabricLoaderVersion>>(loaderVersionsJson)
                            .filter { it.stable }
                            .sortedWith(
                                compareBy(
                                    { it.version.split(it.separator)[0] },
                                    { it.version.split(it.separator)[1] },
                                    { it.version.split(it.separator)[2] },
                                ),
                            ).asReversed()[0]
                            .version
                    Fabric(minecraftVersion, latestLoaderVersion).get()
                }

            /**
             * Using [Mohist](https://mohistmc.com/software/mohist) as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Mohist("1.20.1"))
             *
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.Mohist("1.20.1", forgeVersion = "47.2.23"))
             * ```
             *
             * @param version [Mohist version](https://mohistmc.com/api/v2/projects/mohist)
             * @return URL
             */
            @Suppress("FunctionName")
            fun Mohist(
                version: String,
                forgeVersion: String? = null,
            ): JarUrl =
                JarUrl {
                    val buildsJson = URL("https://mohistmc.com/api/v2/projects/mohist/$version/builds").readText()
                    val predicates =
                        buildList<(MohistBuilds.Build) -> Boolean> {
                            if (forgeVersion != null) {
                                add {
                                    it.forgeVersion == forgeVersion
                                }
                            }
                        }
                    val build =
                        json.decodeFromString<MohistBuilds>(buildsJson).builds.lastOrNull {
                            predicates.all { predicate ->
                                predicate.invoke(it)
                            }
                        } ?: throw NotFoundMohistBuildException(version, forgeVersion)
                    build.originUrl
                }

            /**
             * Using local file as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.LocalFile(projectDir.resolve("server.jar")))
             * ```
             *
             * @param path file path
             * @return URL
             */
            @Suppress("FunctionName")
            fun LocalFile(path: Path): JarUrl =
                JarUrl {
                    "file://${path.toAbsolutePath()}"
                }

            /**
             * Using local file as [jarUrl].
             *
             * ```
             * jarUrl.set(LaunchMinecraftServerTask.JarUrl.LocalFile(projectDir.resolve("server.jar")))
             * ```
             *
             * @param file file
             * @return URL
             */
            @Suppress("FunctionName")
            fun LocalFile(file: File): JarUrl = LocalFile(file.toPath())
        }
    }
}
