package dev.s7a.gradle.minecraft.server.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MinecraftServerPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.s7a.gradle.minecraft.server.plugin")

        assert(project.tasks.getByName("launchMinecraftServer") is LaunchMinecraftServerTask)
    }

    @Test
    fun `extension templateExampleConfig is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.s7a.gradle.minecraft.server.plugin")

        assertNotNull(project.extensions.getByName("minecraftServerConfig"))
    }

    @Test
    fun `parameters are passed correctly from extension to task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.s7a.gradle.minecraft.server.plugin")
        (project.extensions.getByName("minecraftServerConfig") as MinecraftServerConfig).apply {
            jarUrl.set("https://google.com")
        }

        val task = project.tasks.getByName("launchMinecraftServer") as LaunchMinecraftServerTask

        assertEquals("https://google.com", task.jarUrl.get())
    }
}
