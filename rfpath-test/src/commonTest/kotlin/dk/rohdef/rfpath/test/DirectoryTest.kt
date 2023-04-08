package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.NewFileError
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

class DirectoryTest : FunSpec({
    coroutineTestScope = true

    test("Create new file") {
        // Given
        val directory = TestDirectoryDefault.createUnsafe(
            "/usr/local",
        )

        // When
        val fileResult = directory.newFile("foo")
        val file2Result = directory.newFile("fish.sh")
        val fileExistsResult = directory.newFile("foo")

        // Then
        val file = fileResult.shouldBeRight()
        val file2 = file2Result.shouldBeRight()

        file shouldBe TestFileDefault.createUnsafe("/usr/local/foo")
        file2 shouldBe TestFileDefault.createUnsafe("/usr/local/fish.sh")
        val fileExists = fileExistsResult.shouldBeLeft()
        fileExists shouldBe
                NewFileError.FileExists("/usr/local/foo")
    }

    xtest("Listing elements in directory") {
        // Given
        val emptyDirectory = TestDirectoryDefault.createUnsafe("/usr/local")
        val directory = TODO("need structure to create complex directory first")

        // When
        val emptyContentsResult = emptyDirectory.list()
//        val contentsResult = directory.list()

        // Then
//        val emptyContents = emptyContentsResult.shouldBeRight()
//        emptyContents shouldContainInOrder listOf()

//        val contents = contentsResult.shouldBeRight()
//        contents shouldContainExactlyInAnyOrder listOf()
    }

    test("Resolve subelement") {
        // Given
        val directory = TestDirectoryDefault.createUnsafe(
            "/usr/local",
        )
        directory.newFile("foo")
            .shouldBeRight()
        // TODO: 08/04/2023 rohdef - handle creation of directories
    }

    xtest("Reading current permissions") {
        // Given
        // TODO Use test generators to do exhaustive testing
    }

    xtest("Setting new permissions") {
        // Given
        val directory = TestDirectoryDefault.createUnsafe(
            "/usr/local",
        )
    }
})