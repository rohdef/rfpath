package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.FileInstance
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath

class OkioFileTest : FunSpec({
    coroutineTestScope = true

    val testHelpers = OkioTestHelpers()
    val fileSystem = testHelpers.fileSystem()

    val testFile = testHelpers.temporaryDirectoryPath
        .resolve(testHelpers.dummyFilename1)

    fun testFile() =
        OkioFile.file(fileSystem, testFile)

    fun testFileUnwrapped() =
        testFile()
            .shouldBeRight()

    context("Representation of a file") {
        test("successfully create file") {
            val file = testFile()
                .shouldBeRight()
            file.absolutePath
                .shouldBe(testFile.toString())
        }

        test("not accept directory") {
            val directoryPath = testHelpers.workingDirectoryPath

            val error = OkioFile.file(fileSystem, directoryPath)
                .shouldBeLeft()
            error
                .shouldBe(FileInstance.EntityIsADirectory(directoryPath.toString()))
        }

        test("not accept non-path") {
            val notAPath = "this is nonsense"
            val directoryPath = testHelpers.workingDirectoryPath
                .resolve(notAPath)

            val error = OkioFile.file(fileSystem, directoryPath)
                .shouldBeLeft()
            error
                .shouldBe(FileInstance.EntityIsNonExisting(directoryPath.toString()))
        }
    }

    context("File contents") {
        test("write to file") {
            val testContent = "This is a test"
            val file = testFileUnwrapped()

            file.write(testContent)
                .shouldBeRight()

            val actual = fileSystem.read(file.absolutePath.toPath()) { readUtf8() }
            actual
                .shouldBe(testContent)
        }

        test("read file data") {
            val testContent = "This is the contents"
            fileSystem.write(testFile, false) {
                writeUtf8(testContent)
            }

            val file = testFileUnwrapped()
            val readContent = file.readText()

            val text = readContent
                .shouldBeRight()
            text shouldBe testContent
        }
    }

    context("File meta data") {
        // TODO: 15/04/2023 rohdef - https://github.com/rohdef/rfpath/issues/13 - cannot be implemented before Okio supports permissions or better support library is found
        
        xcontext("Permissions") {
            xtest("Current permissions") {
            }

            xtest("Change permissions") {
            }
        }
    }
})