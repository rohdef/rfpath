package dk.rohdef.rfpath.okio

import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.posix.readlink


actual fun OkioFileSystem.Companion.fileSystem(): FileSystem = FileSystem.SYSTEM

actual fun OkioFileSystem.Companion.applicationDirecrtory(): okio.Path {
    return executableFile()
        .toPath()
        .parent!!
}

private fun executableFile(): String {
    return posixReadlink("/proc/self/exe")
        ?: posixReadlink("/proc/curproc/file")
        ?: posixReadlink("/proc/self/path/a.out")
        ?: "./a.out"
}

private fun posixReadlink(path: String): String? = memScoped {
    val addr = allocArray<ByteVar>(PATH_MAX)
    val finalSize = readlink(path, addr, PATH_MAX.convert()).toInt()
    if (finalSize < 0) {
        null
    } else {
        addr.toKString()
    }
}