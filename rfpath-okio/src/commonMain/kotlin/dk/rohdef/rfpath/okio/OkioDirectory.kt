package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import arrow.core.traverse
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions
import korlibs.io.file.VfsFile
import korlibs.io.file.getUnixPermission
import korlibs.io.file.setUnixPermission
import korlibs.io.file.std.localVfs
import okio.FileSystem
import okio.IOException

class OkioDirectory private constructor(
    private val fileSystem: FileSystem,
    private val path: okio.Path
) : Path.Directory {
    override val directoryName: String = path.name.ifBlank { "/" }
    override val absolutePath: String = fileSystem.canonicalize(path).toString()

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        vfs.setUnixPermission(permissions.toVfsPermissions())

        return this.right()
    }

    override suspend fun currentPermissions(): Permissions {
        return vfs.getUnixPermission().toPermissions()
    }

    private val vfs: VfsFile = localVfs(path.toString(), true)

    override suspend fun list(): Either<PathError<*>, List<Path<*, *>>> {
        return fileSystem.list(path)
            .traverse {
                val metadata = fileSystem.metadataOrNull(it)

                if (metadata == null) {
                    DirectoryInstance.EntityIsNonExisting(it.toString()).left()
                } else if (metadata.isDirectory) {
                    directory(fileSystem, it)
                } else {
                    OkioFile.file(fileSystem, it)
                }
            }
    }

    override suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, Path.Directory> {
        val newDirectoryPath = path.resolve(directoryName)

        return try {
            fileSystem.createDirectories(newDirectoryPath, true)
            OkioDirectory(fileSystem, newDirectoryPath).right()
        } catch (exception: IOException) {
            if (exception.message?.contains("already exist") ?: false) {
                MakeDirectoryError.DirectoryExists(newDirectoryPath.toString()).left()
            } else {
                throw exception
            }
        }
    }

    override suspend fun makeFile(fileName: String): Either<MakeFileError, Path.File> {
        return either {
            val file = OkioFile.createFile(fileSystem, path.resolve(fileName))
                .bind()

            file
        }
    }

    override suspend fun resolve(subpath: String): Either<ResolveError, Path<*, *>> {
        val resolvedPath = path.resolve(subpath)

        val metadata = fileSystem.metadataOrNull(resolvedPath)

        return if (metadata == null) {
            ResolveError.ResourceNotFound(resolvedPath.toString()).left()
        } else if (metadata.isDirectory) {
            directory(fileSystem, resolvedPath)
                .mapLeft {
                    when (it) {
                        is DirectoryInstance.EntityIsAFile -> ResolveError.BadResourceResolved
                        is DirectoryInstance.EntityIsNonExisting -> ResolveError.ResourceNotFound(resolvedPath.toString())
                    }
                }
        } else {
            OkioFile.file(fileSystem, resolvedPath)
                .mapLeft {
                    when (it) {
                        is FileInstance.EntityIsADirectory -> ResolveError.BadResourceResolved
                        is FileInstance.EntityIsNonExisting -> ResolveError.ResourceNotFound(resolvedPath.toString())
                    }
                }
        }
    }

    override fun toString(): String {
        return "OkioDirectory(path=$path)"
    }

    companion object {
        fun directory(fileSystem: FileSystem, path: okio.Path): Either<DirectoryInstance, Path.Directory> {
            val metadata = fileSystem.metadataOrNull(path)

            return if (metadata == null) {
                DirectoryInstance.EntityIsNonExisting(path.toString()).left()
            } else if (metadata.isDirectory) {
                OkioDirectory(fileSystem, path).right()
            } else {
                DirectoryInstance.EntityIsAFile(path.toString()).left()
            }
        }
    }
}