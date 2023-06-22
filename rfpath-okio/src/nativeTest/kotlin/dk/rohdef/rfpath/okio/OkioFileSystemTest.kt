package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.FileSystem
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf

class OkioFileSystemTest : FunSpec({
    coroutineTestScope = true

    val testHelpers = OkioTestHelpers()
    val okioFileSystem = runBlocking {
        testHelpers.fileSystem()
    }
    val fileSystem: FileSystem = OkioFileSystem.createPathUtilityUnsafe(
        okioFileSystem,
        testHelpers.applicationDirectoryPath,
        testHelpers.workingDirectoryPath,
        testHelpers.temporaryDirectoryPath,
    )

    test("create temporary file") {
        val temporaryFile = fileSystem.createTemporaryFile()

        val file = temporaryFile.shouldBeRight()
        file
            .shouldBeInstanceOf<Path.File>()
        file.absolutePath
            .shouldStartWith(testHelpers.temporaryDirectory)
    }

    test("create named temporary file") {
        val fileName = "my-fancy-name"
        val temporaryFile = fileSystem.createTemporaryFile(fileName)

        val file = temporaryFile.shouldBeRight()
        file
            .shouldBeInstanceOf<Path.File>()
        file.absolutePath
            .shouldBe("${testHelpers.temporaryDirectory}/$fileName")
    }

    test("create named temporary file that already exists") {
        val fileName = "my-fancy-name"
        fileSystem.createTemporaryFile(fileName).shouldBeRight()
        val temporaryFile = fileSystem.createTemporaryFile(fileName)

        val file = temporaryFile.shouldBeLeft()
        file
            .shouldBeInstanceOf<MakeFileError.FileExists>()
    }

    test("application directory") {
        val applicationDirectory = fileSystem.applicationDirectory()
            .shouldBeRight()

        applicationDirectory.absolutePath
            .shouldBe(testHelpers.applicationDirectory)
        applicationDirectory
            .shouldBeInstanceOf<Path.Directory>()
    }

    test("working directory") {
        val workDirectory = fileSystem.workDirectory()
            .shouldBeRight()

        workDirectory.absolutePath
            .shouldBe(testHelpers.workingDirectory)
        workDirectory
            .shouldBeInstanceOf<Path.Directory>()
    }

    test("root directory") {
        val rootDirectory = fileSystem.root()
            .shouldBeRight()

        rootDirectory.absolutePath
            .shouldBe(testHelpers.rootDirectory)
        rootDirectory
            .shouldBeInstanceOf<Path.Directory>()
    }
})