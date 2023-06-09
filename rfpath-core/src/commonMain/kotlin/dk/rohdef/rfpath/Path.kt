package dk.rohdef.rfpath

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.traverse
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.permissions.UserGroup

/**
 * Basic path components such as a File or Directory.
 * See the Path.Directory and Path.File interfaces that has these common abstractions
 */
sealed interface Path<T : Path<T, E>, E: PathError<T>> {
    val absolutePath: String

    suspend fun addPermission(userGroup: UserGroup, permission: Permission): Either<E, T> {
        val updatedPermissions = currentPermissions()
            .addPermission(userGroup, permission)

        return setPermissions(updatedPermissions)
    }

    suspend fun removePermission(userGroup: UserGroup, permission: Permission): Either<E, T> {
        val updatedPermissions = currentPermissions()
            .removePermission(userGroup, permission)
        return setPermissions(updatedPermissions)
    }

    suspend fun setPermissions(userGroup: UserGroup, permissions: Set<Permission>): Either<E, T> {
        val updatedPermissions = currentPermissions()
            .changePermissions(userGroup, permissions)
        return setPermissions(updatedPermissions)
    }

    suspend fun addPermissions(permissions: Permissions): Either<E, T> = either {
        permissions.owner
            .traverse { addPermission(UserGroup.OWNER, it) }
            .bind()

        TODO()
    }

    suspend fun removePermissions(permissions: Permissions): Either<E, T> {
        TODO()
    }

    suspend fun setPermissions(permissions: Permissions): Either<E, T>

    suspend fun currentPermissions(): Permissions

    interface Directory : Path<Directory, DirectoryError> {
        val directoryName: String

        suspend fun list(): Either<PathError<*>, List<Path<*, *>>>

        suspend fun makeFile(fileName: String): Either<MakeFileError, File>

        suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, Directory>

        suspend fun resolve(vararg subpath: String): Either<ResolveError, Path<*, *>>
    }

    interface File : Path<File, FileError> {
        val fileName: String

        suspend fun readText(): Either<FileError, String>

        suspend fun write(text: String): Either<FileError, File>
    }
}