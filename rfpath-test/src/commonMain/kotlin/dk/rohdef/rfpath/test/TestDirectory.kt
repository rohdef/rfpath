package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestDirectory<SelfType : TestDirectory<SelfType>>(
    val path: List<String>
) : Path.Directory {
    val contents = mutableMapOf<String, Path<*, *>>()

    override val absolutePath: String
        get() = "/${path.joinToString("/")}"

    override suspend fun list(): Either<DirectoryError, List<Path<*, *>>> {
        return contents.map { it.value }.right()
    }

    override abstract suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, SelfType>

    override suspend fun makeFile(fileName: String): Either<MakeFileError, TestFile<*>> {
        if (contents.containsKey(fileName)) {
            return MakeFileError.FileExists("$absolutePath/$fileName").left()
        }

        val file = TestFileDefault.createUnsafe(path + fileName)
        contents.put(fileName, file)
        return file.right()
    }

    override suspend fun resolve(subpath: String): Either<ResolveError, Path<*, *>> {
        return contents.get(subpath)
            ?.right()
            ?: ResolveError.ResourceNotFound("$absolutePath/$subpath").left()
    }

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        TODO("not implemented")
    }

    override suspend fun currentPermissions(): Permissions {
        TODO("not implemented")
    }
}