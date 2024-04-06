package dev.s7a.gradle.minecraft.server.exception

class NotFoundMohistBuildException(
    version: String,
    forgeVersion: String?
) : RuntimeException("Not found build (version: $version, forgeVersion: $forgeVersion)")
