package dev.s7a.gradle.minecraft.server.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class AgreeMinecraftEULATask : DefaultTask() {
    init {
        group = "minecraft"
        description = "Agree to the Minecraft EULA (run after launchMinecraftServer)"
    }

    @get:Input
    @get:Option(option = "serverDirectory", description = "For storing server data.")
    abstract val serverDirectory: DirectoryProperty

    @TaskAction
    fun agreeAction() {
        val serverDirectory = serverDirectory.get().asFile.apply(File::mkdirs)
        val eulaFile = serverDirectory.resolve("eula.txt")
        if (eulaFile.exists() && eulaFile.isFile) {
            logger.lifecycle(
                """
                    Read: https://account.mojang.com/documents/minecraft_eula
                    Do you agree to the EULA? (yes[Y] / no[N])
                """.trimIndent()
            )
            val answer = readLine()
            if (answer.equals("y", true) || answer.equals("yes", true)) {
                val content = eulaFile.readText()
                eulaFile.writeText(content.replace("eula=false", "eula=true"))
            }
        } else {
            logger.lifecycle("agreeMinecraftEULA task skipped.")
        }
    }
}
