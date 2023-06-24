package dk.rohdef.rfpath.utility

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import korlibs.io.util.UUID

/**
 * Represents access to the file system giving access to the most common paths
 */
interface FileSystem {
    /**
     * Create a new temporary file with a random file name
     */
    suspend fun createTemporaryFile(): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        val uuid = UUID.randomUUID().toString()
        return createTemporaryFile(uuid)
    }


    /**
     * Create a new temporary file with the specified file name
     */
    suspend fun createTemporaryFile(fileNmae: String): Either<PathUtilityError.CreateTemporaryFileError, Path.File>

    /**
     * Get the directory from where the application is being run
     */
    suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory>
    suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory>

    /**
     * Get the root of the file system, this would normally be "/"
     *
     * The logic for Windows and other DOS derived structures for filesystems has yet to be determined
     */
    suspend fun root(): Either<DirectoryInstance, Path.Directory>
}