package io.github.kdy05.physicalFighters.api

interface VersionedAdapter {

    fun getSupportedVersion(): String

    fun isCompatible(serverVersion: String): Boolean
}
