package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.NewFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestDirectory(
    override val absolutePath: String
) : Path.Directory {
    val contents = mutableMapOf<String, Path<*, *>>()

    override suspend fun list(): Either<DirectoryError, List<Path<*, *>>> {
        TODO("not implemented")
    }

    override suspend fun newFile(fileName: String): Either<NewFileError, Path.File> {
        if (contents.containsKey(fileName)) {
            return NewFileError.FileExists("$absolutePath/$fileName").left()
        }

        val file = TestFileDefault.createUnsafe("$absolutePath/$fileName")
        contents.put(fileName, file)
        return file.right()
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
}