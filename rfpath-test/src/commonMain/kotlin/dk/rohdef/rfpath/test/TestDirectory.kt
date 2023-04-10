package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestDirectory(
    val path: List<String>
) : Path.Directory {
    val contents = mutableMapOf<String, Path<*, *>>()

    override val absolutePath: String
        get() = "/${path.joinToString("/")}"

    override suspend fun list(): Either<DirectoryError, List<Path<*, *>>> {
        return contents.map { it.value }.right()
    }

    override suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, TestDirectoryDefault> {
        if (contents.containsKey(directoryName)) {
            return MakeDirectoryError.DirectoryExists("$absolutePath/$directoryName").left()
        }

        val directory = TestDirectoryDefault.createUnsafe(path + directoryName)
        contents.put(directoryName, directory)
        return directory.right()
    }

    override suspend fun makeFile(fileName: String): Either<MakeFileError, TestFileDefault> {
        if (contents.containsKey(fileName)) {
            return MakeFileError.FileExists("$absolutePath/$fileName").left()
        }

        val file = TestFileDefault.createUnsafe(path + fileName)
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