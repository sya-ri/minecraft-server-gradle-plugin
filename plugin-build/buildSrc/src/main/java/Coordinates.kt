object PluginCoordinates {
    const val ID = "dev.s7a.gradle.minecraft.server"
    const val GROUP = "dev.s7a.gradle.minecraft.server"
    const val VERSION = "3.2.1"
    const val IMPLEMENTATION_CLASS = "dev.s7a.gradle.minecraft.server.MinecraftServerPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/sya-ri/minecraft-server-gradle-plugin"
    const val WEBSITE = "https://github.com/sya-ri/minecraft-server-gradle-plugin"
    const val DESCRIPTION = "Launch Minecraft servers using Gradle task. For Bukkit, Spigot, Paper, etc.."
    const val DISPLAY_NAME = "Launch Minecraft Servers Plugin"
    val TAGS =
        listOf(
            "minecraft",
            "bukkit",
            "spigot",
            "paper",
        )
}
