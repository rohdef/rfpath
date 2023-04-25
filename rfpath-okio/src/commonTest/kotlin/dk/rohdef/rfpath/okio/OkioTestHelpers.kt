package dk.rohdef.rfpath.okio

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.getOrHandle
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.test.TestDirectory
import dk.rohdef.rfpath.test.TestFile
import dk.rohdef.rfpath.test.root
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class OkioTestHelpers {
    class OkioTestDirectory(
        private val directory: OkioDirectory,
        path: List<String>,
    ) : TestDirectory<OkioTestDirectory>(path), dk.rohdef.rfpath.Path.Directory by directory {
        override suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, OkioTestDirectory> =
            either {
                val newDirectory = directory.makeDirectory(directoryName)
                    .mapLeft { TODO("Look into the error cases and what to do") }
                    .bind() as OkioDirectory

                OkioTestDirectory(newDirectory, path + directoryName)
            }

        override suspend fun list(): Either<DirectoryError, List<dk.rohdef.rfpath.Path<*, *>>> {
            return directory.list()
                .mapLeft { TODO("Look into the error cases and what to do") }
        }

        override suspend fun makeFile(fileName: String): Either<MakeFileError, TestFile<*>> {
            TODO("Handle creating proper OkioTestFiles")
        }
    }

    private suspend fun x() {
        root({
            directory("poc") {}
        }) {
            // TODO yet another nasty cast
            OkioTestDirectory(
                OkioDirectory.directory(fileSystem(), "/".toPath())
                    .getOrHandle { TODO() }
                        as OkioDirectory,
                it,
            )
        }
    }


    private val root = "/".toPath()
    val basePath = root.resolve("test")

    val applicationDirectoryPath = basePath
        .resolve("usr")
        .resolve("local")
        .resolve("bin")
    val workingDirectoryPath = basePath
        .resolve("home")
        .resolve("fiktivus")
    val temporaryDirectoryPath = basePath
        .resolve("tmp")

    val applicationDirectory = applicationDirectoryPath.toString()
    val workingDirectory = workingDirectoryPath.toString()
    val rootDirectory = root.toString()
    val temporaryDirectory = temporaryDirectoryPath.toString()

    val dummySubDirectory = "folder"

    val dummyFilename1 = "hello.world"
    val dummyFilename2 = "foo"
    val dummyFilename3 = "multi-line"

    fun fileSystem(): FileSystem {
        val fileSystem = FakeFileSystem()

        pathWithDummyFiles(fileSystem, basePath)
        pathWithDummyFiles(fileSystem, applicationDirectoryPath)
        pathWithDummyFiles(fileSystem, workingDirectoryPath)
        pathWithDummyFiles(fileSystem, temporaryDirectoryPath)

        pathWithDummyDirectories(fileSystem, temporaryDirectoryPath)

        fileSystem.workingDirectory = workingDirectoryPath

        return fileSystem
    }

    private fun pathWithDummyDirectories(fileSystem: FileSystem, path: Path) {
        fileSystem.createDirectories(path.resolve(dummySubDirectory), true)
    }

    private fun pathWithDummyFiles(fileSystem: FileSystem, path: Path) {
        fileSystem.createDirectories(path, true)
        dummyFiles(fileSystem, path)
    }

    private fun dummyFiles(fileSystem: FileSystem, path: Path) {
        fileSystem.write(path.resolve(dummyFilename1), true) {
            writeUtf8("Hello world!")
        }
        fileSystem.write(path.resolve(dummyFilename2), true) {
            writeUtf8("barbaz")
        }
        fileSystem.write(path.resolve(dummyFilename3), true) {
            writeUtf8(
                """
                Multiple
                Lines
                In
                File
            """.trimIndent()
            )
        }
    }
}