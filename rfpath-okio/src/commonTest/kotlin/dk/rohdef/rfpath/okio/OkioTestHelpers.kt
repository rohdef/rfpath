package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.test.builders.DirectoryContext
import dk.rohdef.rfpath.test.builders.root
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem

class OkioTestHelpers {
     private suspend fun linuxishSystem() : FakeFileSystem =
        fileRoot {
            directory("bin") {}

            directory("etc") {}

            directory("home") {
                directory("fiktivus") {
                    dummyFiles()
                }
            }
            directory("tmp") {
                directory(dummySubDirectory) {}
                dummyFiles()
            }

            directory("usr") {
                directory("local") {
                    directory("bin") {
                        dummyFiles()
                    }
                }
            }
        }

    private suspend fun fileRoot(configure: DirectoryContext<Path>.() -> Unit) : FakeFileSystem {
        val fileSystem = FakeFileSystem()

        root(
            "/".toPath(),
            { parentPath, directoryContext ->
                val subpath = parentPath.resolve(directoryContext.path.last())
                fileSystem.createDirectories(subpath, true)
                subpath
            },
            { parentPath, fileContext ->
                val subpath = parentPath.resolve(fileContext.fileName)
                fileSystem.write(subpath, true) {
                    writeUtf8(fileContext.contents)
                }
            },
            configure,
        )

        return fileSystem
    }

    private fun DirectoryContext<Path>.dummyFiles() {
        file(dummyFilename1) {
            contents = "Hello world!"
        }

        file(dummyFilename2) {
            contents = "barbaz"
        }

        file(dummyFilename3) {
            contents = """
                Multiple
                Lines
                In
                File
            """.trimIndent()
        }
    }

    val root = "/".toPath()

    val applicationDirectoryPath = root
        .resolve("usr")
        .resolve("local")
        .resolve("bin")
    val workingDirectoryPath = root
        .resolve("home")
        .resolve("fiktivus")
    val temporaryDirectoryPath = root
        .resolve("tmp")

    val applicationDirectory = applicationDirectoryPath.toString()
    val workingDirectory = workingDirectoryPath.toString()
    val rootDirectory = root.toString()
    val temporaryDirectory = temporaryDirectoryPath.toString()

    val dummySubDirectory = "folder"

    val dummyFilename1 = "hello.world"
    val dummyFilename2 = "foo"
    val dummyFilename3 = "multi-line"

    suspend fun fileSystem(): FileSystem {
        val fileSystem = linuxishSystem()

        fileSystem.workingDirectory = workingDirectoryPath

        return fileSystem
    }
}