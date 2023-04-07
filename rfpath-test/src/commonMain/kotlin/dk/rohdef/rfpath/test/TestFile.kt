package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.right
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.FileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

class TestFile private constructor(
    override val absolutePath: String,
    var permissions: Permissions,
) : Path.File {
    var contents = ""

    override suspend fun readText(): Either<FileError, String> {
        return contents.right()
    }

    override suspend fun write(text: String): Either<FileError, TestFile> {
        contents = text
        return this.right()
    }

    override suspend fun setPermissions(permissions: Permissions): Either<FileError, TestFile> {
        this.permissions = permissions
        return this.right()
    }

    override suspend fun currentPermissions(): Permissions {
        return permissions
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
            return TestFile(absolutePath, permissions)
        }
    }
}