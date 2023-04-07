package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.right
import dk.rohdef.rfpath.FileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestFile<T : TestFile<T>>(
    override val absolutePath: String,
    var permissions: Permissions,
) : Path.File {
    private val self = (this as T)
    var contents = ""

    override suspend fun readText(): Either<FileError, String> {
        return contents.right()
    }

    override suspend fun write(text: String): Either<FileError, T> {
        contents = text
        return self.right()
    }

    override suspend fun setPermissions(permissions: Permissions): Either<FileError, T> {
        this.permissions = permissions
        return self.right()
    }

    override suspend fun currentPermissions(): Permissions {
        return permissions
    }
}