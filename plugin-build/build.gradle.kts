import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("com.gradle.plugin-publish") version "0.21.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
    id("com.github.ben-manes.versions") version "0.47.0"
}

allprojects {
    group = PluginCoordinates.GROUP
    version = PluginCoordinates.VERSION

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    ktlint {
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    detekt {
        config = rootProject.files("../config/detekt/detekt.yml")
        reports {
            html {
                enabled = true
                destination = file("build/reports/detekt.html")
            }
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}
