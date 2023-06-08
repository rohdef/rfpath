package dk.rohdef.rfpath.test

import arrow.core.*
import dk.rohdef.rfpath.*
import dk.rohdef.rfpath.permissions.Permissions

abstract class TestDirectory<SelfType : TestDirectory<SelfType>>(
    val path: List<String>,
    var permissions: Permissions,
) : Path.Directory {
    val contents = mutableMapOf<String, Path<*, *>>()

    override val directoryName: String = path.lastOrNone().getOrElse { "/" }
    override val absolutePath: String = "/${path.joinToString("/")}"

    override suspend fun list(): Either<DirectoryError, List<Path<*, *>>> {
        return contents.map { it.value }.right()
    }

    override abstract suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, SelfType>

    override suspend fun makeFile(fileName: String): Either<MakeFileError, TestFile<*>> {
        if (contents.containsKey(fileName)) {
            return MakeFileError.FileExists("$absolutePath/$fileName").left()
        }

        val file = TestFileDefault.createUnsafe((path + fileName).toNonEmptyListOrNull()!!)
        contents.put(fileName, file)
        return file.right()
    }

    override suspend fun resolve(vararg subpath: String): Either<ResolveError, Path<*, *>> {
        val path = subpath.toList()
        val first = path.first()
        val rest = path.subList(1, path.size)


        val firstPart = contents.get(first)
                ?.right()
                ?: ResolveError.ResourceNotFound("$absolutePath/$first").left()

        return if (rest.size > 0)  {
             firstPart.flatMap {
                 return when (it) {
                     is Path.Directory -> it.resolve(*rest.toTypedArray())
                     is Path.File -> ResolveError.BadResourceResolved.left()
                 }
             }
        } else {
            firstPart
        }
    }

    override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.Directory> {
        this.permissions = permissions
        return this.right()
    }

    override suspend fun currentPermissions(): Permissions = permissions
}