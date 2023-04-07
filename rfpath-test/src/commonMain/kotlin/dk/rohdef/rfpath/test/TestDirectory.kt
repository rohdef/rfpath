package dk.rohdef.rfpath.test

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.NewFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

class TestDirectory private constructor(
    override val absolutePath: String
): Path.Directory {
    override suspend fun list(): Either<DirectoryError, List<Path<*, *>>> {
        TODO("not implemented")
    }

    override suspend fun newFile(fileName: String): Either<NewFileError, Path.File> {
        TODO("not implemented")
    }

    override suspend fun resolve(subpath: String): Either<DirectoryError, Path<*, *>> {
        TODO("not implemented")
    }

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        TODO("not implemented")
    }

    override suspend fun currentPermissions(): Permissions {
        TODO("not implemented")
    }

    companion object {
        fun createUnsafe(): TestDirectory {
            TODO()
        }
    }
}