package dk.rohdef.rfpath.okio

import dk.rohdef.rfpath.DirectoryInstance
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Ignore
import kotlin.test.Test

@ExperimentalCoroutinesApi
class OkioDirectoryTest {
    private val testHelpers = OkioTestHelpers()
    private val fileSystem = testHelpers.fileSystem()

    @Test
    fun `Directory should work`() = runTest {
        val directory = testDirectory()
            .shouldBeRight()
        directory.absolutePath
            .shouldBe(testHelpers.temporaryDirectory)
    }

    @Test
    fun `File should result in error`() = runTest {
        val filePath = testHelpers.workingDirectoryPath
            .resolve(testHelpers.dummyFilename1)

        val error = OkioDirectory.directory(fileSystem, filePath)
            .shouldBeLeft()
        error
            .shouldBe(DirectoryInstance.EntityIsAFile(filePath.toString()))
    }

    @Test
    fun `Not a path should result in error`() = runTest {
        val notAPath = "this is nonsense"
        val directoryEither = OkioDirectory.directory(fileSystem, notAPath.toPath())

        val directoryError = directoryEither
            .shouldBeLeft()
        directoryError
            .shouldBe(DirectoryInstance.EntityIsNonExisting(notAPath))
    }

    @Test
    @Ignore
    fun `Listing files`() = runTest {
        val directory = testDirectoryUnwrapped()

    }

    @Test
    fun `Making a new directory`() = runTest {
        val directory = testDirectoryUnwrapped()
            .makeDirectory("new-directory")
            .shouldBeRight()

        val expectedDirectoryPath = testHelpers.temporaryDirectoryPath
            .resolve("new-directory")
            .toString()
        directory.absolutePath
            .shouldBe(expectedDirectoryPath)
    }

    @Test
    fun `Make directory when directory exists`() = runTest {
        val error = testDirectoryUnwrapped()
            .makeDirectory(testHelpers.dummySubDirectory)
            .shouldBeLeft()

        val temporaryDirectory = testHelpers.temporaryDirectoryPath
        val attemptedDirectory = temporaryDirectory.resolve(testHelpers.dummySubDirectory)
        val expectedError = MakeDirectoryError.DirectoryExists(attemptedDirectory.toString())
        error
            .shouldBe(expectedError)
    }

    @Test
    fun `Making a new file`() = runTest {
        val file = testDirectoryUnwrapped()
            .makeFile("new-file")
            .shouldBeRight()

        val expectedFilePath = testHelpers.temporaryDirectoryPath
            .resolve("new-file")
            .toString()
        file.absolutePath
            .shouldBe(expectedFilePath)
    }

    @Test
    fun `Make file when file exists`() = runTest {
        val error = testDirectoryUnwrapped()
            .makeFile(testHelpers.dummyFilename3)
            .shouldBeLeft()

        val temporaryDirectory = testHelpers.temporaryDirectoryPath
        val attemptedFile = temporaryDirectory.resolve(testHelpers.dummyFilename3)
        val expectedError = MakeFileError.FileExists(attemptedFile.toString())
        error
            .shouldBe(expectedError)
    }

    @Test
    @Ignore
    fun `Current permissions`() = runTest {
        TODO("Currently untestable due to okio not having a system for permissions")
    }

    @Test
    @Ignore
    fun `Change permissions`() = runTest {
        TODO("Currently untestable due to okio not having a system for permissions")
    }

    private fun testDirectoryUnwrapped() =
        testDirectory()
            .shouldBeRight()

    private fun testDirectory() =
        OkioDirectory.directory(fileSystem, testHelpers.temporaryDirectoryPath)
}