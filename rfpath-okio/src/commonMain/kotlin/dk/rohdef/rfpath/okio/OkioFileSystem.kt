package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.right
import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.FileSystem
import dk.rohdef.rfpath.utility.PathUtilityError
import korlibs.io.util.UUID
import okio.Path.Companion.toPath

class OkioFileSystem(
    private val fileSystem: okio.FileSystem,
    private val applicationDirectory: okio.Path,
    private val workDirectory: okio.Path,
    private val temporaryDirectory: okio.Path,
) : FileSystem {
    override suspend fun root(): Either<DirectoryInstance, Path.Directory> =
        OkioDirectory.directory(fileSystem, "/".toPath())

    override suspend fun applicationDirectory(): Either<DirectoryInstance, Path.Directory> =
        OkioDirectory.directory(fileSystem, applicationDirectory)

    override suspend fun workDirectory(): Either<DirectoryInstance, Path.Directory> =
        OkioDirectory.directory(fileSystem, workDirectory)

    override suspend fun createTemporaryFile(fileNmae: String): Either<PathUtilityError.CreateTemporaryFileError, Path.File> {
        return either {
            val directory = OkioDirectory.directory(fileSystem, temporaryDirectory)
                .mapLeft { PathUtilityError.CreateTemporaryFileError.CannotGetTemporaryDirectory }
                .bind()

            directory
                .makeFile(fileNmae)
                .mapLeft { PathUtilityError.CreateTemporaryFileError.CannotCreateFile }
                .bind()

        }
    }

    companion object {
        fun createPathUtility(
            fileSystem: okio.FileSystem = fileSystem(),
            applicationDirectory: okio.Path = applicationDirecrtory(),
            workDirectory: okio.Path = ".".toPath(),
            temporaryDirectory: okio.Path = okio.FileSystem.SYSTEM_TEMPORARY_DIRECTORY,
        ): Either<Unit, FileSystem> {
            return OkioFileSystem(
                fileSystem,
                applicationDirectory,
                workDirectory,
                temporaryDirectory
            ).right()
        }

        fun createPathUtilityUnsafe(
            fileSystem: okio.FileSystem = fileSystem(),
            applicationDirectory: okio.Path = applicationDirecrtory(),
            workDirectory: okio.Path = ".".toPath(),
            temporaryDirectory: okio.Path = okio.FileSystem.SYSTEM_TEMPORARY_DIRECTORY,
        ): FileSystem {
            val utility = createPathUtility(
                fileSystem,
                applicationDirectory,
                workDirectory,
                temporaryDirectory,
            )

            when (utility) {
                is Either.Right -> return utility.value
                is Either.Left -> throw IllegalArgumentException("Could not create path utility: ${utility}")
            }
        }
    }
}

expect fun OkioFileSystem.Companion.fileSystem(): okio.FileSystem
expect fun OkioFileSystem.Companion.applicationDirecrtory(): okio.Path