package dev.s7a.gradle.minecraft.server.exception

class UnsupportedProtocolException(
    protocol: String,
) : RuntimeException("Unsupported protocol: $protocol")
