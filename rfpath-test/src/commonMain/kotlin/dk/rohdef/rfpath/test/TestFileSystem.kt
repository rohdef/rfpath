package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.FileSystem
import dk.rohdef.rfpath.utility.PathUtilityError

class TestFileSystem(
    private val root: Path.Directory,
    private val application: Path.Directory,
    private val workDirectory: Path.Directory,
    private val temporary: Path.Directory,
) : FileSystem {
    override suspend fun root(): Either<DirectoryInstance, Path.Directory> = root.right()

    override suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        return temporary.makeFile("yay")
            .getOrElse { throw IllegalStateException("") }
            .right()
    }

    override suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory> {
        return application.right()
    }

    override suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory> {
        return workDirectory.right()
    }
}