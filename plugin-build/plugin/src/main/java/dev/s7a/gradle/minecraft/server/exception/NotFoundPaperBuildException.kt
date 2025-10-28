package dev.s7a.gradle.minecraft.server.exception

class NotFoundPaperBuildException(
    version: String,
    type: String,
) : RuntimeException("Not found build (version: $version, type: $type)")
