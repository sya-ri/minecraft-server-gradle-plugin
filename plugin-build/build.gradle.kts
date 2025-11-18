import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21" apply false
    id("com.gradle.plugin-publish") version "2.0.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jmailen.kotlinter") version "5.3.0"
    id("com.github.ben-manes.versions") version "0.53.0"
}

allprojects {
    group = PluginCoordinates.GROUP
    version = PluginCoordinates.VERSION

    repositories {
        google()
        mavenCentral()
    }

    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jmailen.kotlinter")
    }

    detekt {
        config.from(rootProject.files("../config/detekt/detekt.yml"))
    }

    tasks.withType<JavaCompile> {
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
