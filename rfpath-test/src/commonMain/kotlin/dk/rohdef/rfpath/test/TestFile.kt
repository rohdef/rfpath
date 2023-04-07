package dk.rohdef.rfpath.test

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.FileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

class TestFile private constructor(
    override val absolutePath: String
) : Path.File {
    var contents = ""

    override suspend fun readText(): Either<FileError, String> {
        TODO("not implemented")
    }

    override suspend fun write(text: String): Either<FileError, TestFile> {
        TODO("not implemented")
    }

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, TestFile> {
        TODO("not implemented")
    }

    override suspend fun currentPermissions(): Permissions {
        TODO("not implemented")
    }

    companion object {
        fun createUnsafe(
            absolutePath: String,
            permissions: Permissions = Permissions(
                owner = setOf(Permission.READ, Permission.WRITE),
                group = setOf(Permission.READ, Permission.WRITE),
                other = emptySet(),
            )
        ): TestFile {
            return TestFile(absolutePath)
        }
    }
}