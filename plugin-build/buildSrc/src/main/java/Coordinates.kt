object PluginCoordinates {
    const val ID = "dev.s7a.gradle.minecraft.server"
    const val GROUP = "dev.s7a.gradle.minecraft.server"
    const val VERSION = "1.2.0"
    const val IMPLEMENTATION_CLASS = "dev.s7a.gradle.minecraft.server.MinecraftServerPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/sya-ri/minecraft-server-gradle-plugin"
    const val WEBSITE = "https://github.com/sya-ri/minecraft-server-gradle-plugin"
    const val DESCRIPTION = "Launch a Minecraft Server Using Gradle Task. For Bukkit, Spigot, Paper, etc.."
    const val DISPLAY_NAME = "Launch Minecraft Server Plugin"
    val TAGS = listOf(
        "plugin",
        "gradle",
        "minecraft",
        "bukkit",
        "spigot",
        "paper"
    )
}
