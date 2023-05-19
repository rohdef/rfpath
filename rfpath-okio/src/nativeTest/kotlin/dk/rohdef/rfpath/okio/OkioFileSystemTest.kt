package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.utility.FileSystem
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