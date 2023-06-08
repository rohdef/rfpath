package dk.rohdef.rfpath.utility

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path

/**
 * Represents access to the file system giving access to the most common paths
 */
interface FileSystem {
    suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File>

    suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory>
    suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory>

    suspend fun root(): Either<DirectoryInstance, Path.Directory>
}