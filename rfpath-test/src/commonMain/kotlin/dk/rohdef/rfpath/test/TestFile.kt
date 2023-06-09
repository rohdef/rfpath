package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.right
import dk.rohdef.rfpath.FileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestFile<SelfType : TestFile<SelfType>>(
    val path: NonEmptyList<String>,
    var permissions: Permissions,
) : Path.File {
    @Suppress("UNCHECKED_CAST")
    private val self = (this as SelfType)
    var contents = ""

    override val fileName: String = path.last()
    override val absolutePath: String = "/${path.joinToString("/")}"

    override suspend fun readText(): Either<FileError, String> {
        return contents.right()
    }

    override suspend fun write(text: String): Either<FileError, SelfType> {
        contents = text
        return self.right()
    }

    override suspend fun setPermissions(permissions: Permissions): Either<FileError, SelfType> {
        this.permissions = permissions
        return self.right()
    }

    override suspend fun currentPermissions(): Permissions {
        return permissions
    }
}