package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.FileError
import dk.rohdef.rfpath.FileInstance
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions
import korlibs.io.file.VfsFile
import korlibs.io.file.getUnixPermission
import korlibs.io.file.setUnixPermission
import korlibs.io.file.std.localVfs
import okio.FileSystem

class OkioFile private constructor(
    val fileSystem: FileSystem,
    val path: okio.Path,
) : Path.File {
    override val fileName: String = path.name
    override val absolutePath: String = path.toString()

    override suspend fun setPermissions(permissions: Permissions): Either<FileError, Path.File> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermissions(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }
    private val vfs: VfsFile = localVfs(path.toString(), true)

    override suspend fun readText(): Either<FileError, String> {
        return fileSystem.read(path) {
            readUtf8()
        }.right()
    }

    override suspend fun write(text: String): Either<FileError, Path.File> {
        fileSystem.write(path, false) { writeUtf8(text) }

        return this.right()
    }

    companion object {
        fun file(fileSystem: FileSystem, path: okio.Path): Either<FileInstance, Path.File> {
            val metadata = fileSystem.metadataOrNull(path)

            return if (metadata == null) {
                FileInstance.EntityIsNonExisting(path.toString()).left()
            } else if (metadata.isDirectory) {
                FileInstance.EntityIsADirectory(path.toString()).left()
            } else {
                OkioFile(fileSystem, path).right()
            }
        }

        fun createFile(fileSystem: FileSystem, path: okio.Path): Either<MakeFileError, Path.File> {
            val metadata = fileSystem.metadataOrNull(path)

            if (metadata != null) {
                return MakeFileError.FileExists(path.toString()).left()
            }

            return OkioFile(fileSystem, path).right()
        }
    }
}