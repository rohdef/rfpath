package dk.rohdef.rfpath.okio

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual fun OkioFileSystem.Companion.applicationDirecrtory(): Path {
    return executableFile()
        .toPath()
        .parent!!
}

private fun executableFile(): String {
    return OkioFileSystem::class.java.getProtectionDomain().getCodeSource().getLocation().path
}

actual fun OkioFileSystem.Companion.fileSystem(): FileSystem = FileSystem.SYSTEM