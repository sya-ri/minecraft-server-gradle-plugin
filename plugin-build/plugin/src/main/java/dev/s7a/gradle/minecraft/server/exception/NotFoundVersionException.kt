package dev.s7a.gradle.minecraft.server.exception

class NotFoundVersionException(version: String, versions: List<String>) : RuntimeException("Not found version $version. Use ${versions.joinToString()}")
