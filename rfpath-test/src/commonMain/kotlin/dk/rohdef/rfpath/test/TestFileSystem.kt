package dk.rohdef.rfpath.test

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.FileSystem
import dk.rohdef.rfpath.utility.PathUtilityError

class TestFileSystem : FileSystem {
    override suspend fun root(): Either<DirectoryInstance, Path.Directory> {
        TODO("not implemented")
    }

    override suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        TODO()
    }

    override suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory> {
        TODO("not implemented")
    }

    override suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory> {
        TODO("not implemented")
    }
}