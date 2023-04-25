package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath

class OkioDirectoryTest : FunSpec({
    coroutineTestScope = true

    val testHelpers = OkioTestHelpers()
    val fileSystem = testHelpers.fileSystem()

    fun testDirectory() =
        OkioDirectory.directory(fileSystem, testHelpers.temporaryDirectoryPath)

    fun testDirectoryUnwrapped() =
        testDirectory()
            .shouldBeRight()

    context("Representation of a directory") {
        test("Directory should work") {
            val directory = testDirectory()
                .shouldBeRight()
            directory.absolutePath
                .shouldBe(testHelpers.temporaryDirectory)

        }

        test("File should result in error") {
            val filePath = testHelpers.workingDirectoryPath
                .resolve(testHelpers.dummyFilename1)

            val error = OkioDirectory.directory(fileSystem, filePath)
                .shouldBeLeft()
            error
                .shouldBe(DirectoryInstance.EntityIsAFile(filePath.toString()))
        }

        test("Not a path should result in error") {
            val notAPath = "this is nonsense"
            val directoryEither = OkioDirectory.directory(fileSystem, notAPath.toPath())

            val directoryError = directoryEither
                .shouldBeLeft()
            directoryError
                .shouldBe(DirectoryInstance.EntityIsNonExisting(notAPath))
        }
    }

    context("Directory contents") {
        context("retrieval") {
            test("Listing all contents") {
                val directory = testDirectoryUnwrapped()

                val contentsFound = directory.list()

                val contents = contentsFound
                    .shouldBeRight()
                contents shouldBe listOf(TODO())
            }

            test("Listing from empty directory") {
                TODO()
                val directory = testDirectoryUnwrapped()

                val contentsFound = directory.list()

                val contents = contentsFound
                    .shouldBeRight()
                contents
                    .shouldBeEmpty()
            }

            test("Resolve directory") {
                val directory = testDirectoryUnwrapped()
            }

            test("Resolve file") {
                val directory = testDirectoryUnwrapped()
            }

            test("Resolve non-existing") {
                val directory = testDirectoryUnwrapped()
            }
        }

        context("manipulation") {
            test("Making a new directory") {
                val directory = testDirectoryUnwrapped()
                    .makeDirectory("new-directory")
                    .shouldBeRight()

                val expectedDirectoryPath = testHelpers.temporaryDirectoryPath
                    .resolve("new-directory")
                    .toString()
                directory.absolutePath
                    .shouldBe(expectedDirectoryPath)
            }

            test("Make directory when directory exists") {
                val error = testDirectoryUnwrapped()
                    .makeDirectory(testHelpers.dummySubDirectory)
                    .shouldBeLeft()

                val temporaryDirectory = testHelpers.temporaryDirectoryPath
                val attemptedDirectory = temporaryDirectory.resolve(testHelpers.dummySubDirectory)
                val expectedError = MakeDirectoryError.DirectoryExists(attemptedDirectory.toString())
                error
                    .shouldBe(expectedError)
            }

            test("Making a new file") {
                val file = testDirectoryUnwrapped()
                    .makeFile("new-file")
                    .shouldBeRight()

                val expectedFilePath = testHelpers.temporaryDirectoryPath
                    .resolve("new-file")
                    .toString()
                file.absolutePath
                    .shouldBe(expectedFilePath)
            }

            test("Make file when file exists") {
                val error = testDirectoryUnwrapped()
                    .makeFile(testHelpers.dummyFilename3)
                    .shouldBeLeft()

                val temporaryDirectory = testHelpers.temporaryDirectoryPath
                val attemptedFile = temporaryDirectory.resolve(testHelpers.dummyFilename3)
                val expectedError = MakeFileError.FileExists(attemptedFile.toString())
                error
                    .shouldBe(expectedError)
            }
        }
    }

    xcontext("Meta data") {
        // TODO: 15/04/2023 rohdef - https://github.com/rohdef/rfpath/issues/13 - cannot be implemented before Okio supports permissions or better support library is found

        test("Current permissions") {}

        test("Change permissions") {}
    }
})