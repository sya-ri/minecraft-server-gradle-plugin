import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("com.gradle.plugin-publish") version "1.3.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("org.jmailen.kotlinter") version "4.4.1"
    id("com.github.ben-manes.versions") version "0.51.0"
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
        config = rootProject.files("../config/detekt/detekt.yml")
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
