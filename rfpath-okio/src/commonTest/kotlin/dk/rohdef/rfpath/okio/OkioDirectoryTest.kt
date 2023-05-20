package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.*
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.Path.Companion.toPath

class OkioDirectoryTest : FunSpec({
    coroutineTestScope = true

    val testHelpers = OkioTestHelpers()

    suspend fun testDirectory() =
        OkioDirectory.directory(testHelpers.fileSystem(), testHelpers.temporaryDirectoryPath)

    suspend fun testDirectoryUnwrapped() =
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

            val error = OkioDirectory.directory(testHelpers.fileSystem(), filePath)
                .shouldBeLeft()
            error
                .shouldBe(DirectoryInstance.EntityIsAFile(filePath.toString()))
        }

        test("Not a path should result in error") {
            val notAPath = "this is nonsense"
            val directoryEither = OkioDirectory.directory(testHelpers.fileSystem(), notAPath.toPath())

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

                contents.filterIsInstance<Path.File>()
                    .map { it.fileName }
                    .shouldContainExactlyInAnyOrder(
                        testHelpers.dummyFilename1,
                        testHelpers.dummyFilename2,
                        testHelpers.dummyFilename3,
                    )
                contents.filterIsInstance<Path.Directory>()
                    .map { it.directoryName }
                    .shouldContainExactlyInAnyOrder(
                        testHelpers.dummySubDirectory,
                    )
            }

            test("Listing from empty directory") {
                val directory = testDirectoryUnwrapped()
                    .resolve(testHelpers.dummySubDirectory)
                    .shouldBeRight()
                    .shouldBeInstanceOf<Path.Directory>()

                val contentsFound = directory.list()

                val contents = contentsFound
                    .shouldBeRight()
                contents
                    .shouldBeEmpty()
            }

            test("Resolve directory") {
                testDirectoryUnwrapped()
                    .resolve(testHelpers.dummySubDirectory)
                    .shouldBeRight()
                    .shouldBeInstanceOf<Path.Directory>()
            }

            test("Resolve file") {
                testDirectoryUnwrapped()
                    .resolve(testHelpers.dummyFilename2)
                    .shouldBeRight()
                    .shouldBeInstanceOf<Path.File>()
            }

            test("Resolve non-existing") {
                testDirectoryUnwrapped()
                    .resolve("nonsense")
                    .shouldBeLeft()
                    .shouldBeInstanceOf<ResolveError.ResourceNotFound>()
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