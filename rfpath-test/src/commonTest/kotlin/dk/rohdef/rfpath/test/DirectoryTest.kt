package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.ResolveError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class DirectoryTest : FunSpec({
    coroutineTestScope = true

    test("Make file") {
        // Given
        val baseDirectory = TestDirectoryDefault.createUnsafe(
            listOf("usr", "local"),
        )

        // When
        val fileResult = baseDirectory.makeFile("foo")
        val file2Result = baseDirectory.makeFile("fish.sh")
        val fileExistsResult = baseDirectory.makeFile("foo")

        // Then
        val file = fileResult.shouldBeRight()
        val file2 = file2Result.shouldBeRight()

        file shouldBe TestFileDefault.createUnsafe(listOf("usr", "local", "foo"))
        file2 shouldBe TestFileDefault.createUnsafe(listOf("usr", "local", "fish.sh"))
        val fileExists = fileExistsResult.shouldBeLeft()
        fileExists shouldBe
                MakeFileError.FileExists("/usr/local/foo")
    }

    test("Make directory") {
        // Given
        val baseDirectory = TestDirectoryDefault.createUnsafe(
            listOf("usr", "local"),
        )

        // When
        val directoryResult = baseDirectory.makeDirectory("bin")
        val directory2Result = baseDirectory.makeDirectory("etc")
        val directoryExistsResult = baseDirectory.makeDirectory("bin")

        // Then
        val directory = directoryResult.shouldBeRight()
        val directory2 = directory2Result.shouldBeRight()

        directory shouldBe TestDirectoryDefault.createUnsafe(listOf("usr", "local", "bin"))
        directory2 shouldBe TestDirectoryDefault.createUnsafe(listOf("usr", "local", "etc"))
        val directoryExists = directoryExistsResult.shouldBeLeft()
        directoryExists shouldBe
                MakeDirectoryError.DirectoryExists("/usr/local/bin")
    }

    test("Listing elements in directory") {
        // Given
        val emptyDirectory = TestDirectoryDefault.createUnsafe(listOf())
        val directory = root {
            directory("bin") {}
            directory("etc") {}
            directory("tmp") {}

            file("buffer") {}
            file("database.db") {}
        }

        // When
        val emptyContentsResult = emptyDirectory.list()
        val contentsResult = directory.list()

        // Then
        val emptyContents = emptyContentsResult.shouldBeRight()
        emptyContents shouldBe emptyList()

        val contents = contentsResult.shouldBeRight()
        contents shouldContainExactlyInAnyOrder listOf(
            TestDirectoryDefault.createUnsafe(listOf("bin")),
            TestDirectoryDefault.createUnsafe(listOf("etc")),
            TestDirectoryDefault.createUnsafe(listOf("tmp")),

            TestFileDefault.createUnsafe(listOf("buffer")),
            TestFileDefault.createUnsafe(listOf("database.db")),
        )
    }

    test("Resolve subelement") {
        // Given
        val baseDirectory = TestDirectoryDefault.createUnsafe(
            listOf("usr", "local"),
        )
        baseDirectory.makeFile("foo")
            .shouldBeRight()

        // When
        val fooResult = baseDirectory.resolve("foo")
        val otherResult = baseDirectory.resolve("other")

        // Then
        val foo = fooResult.shouldBeRight()
        foo shouldBe TestFileDefault.createUnsafe(listOf("usr", "local", "foo"))

        val other = otherResult.shouldBeLeft()
        other shouldBe ResolveError.ResourceNotFound("/usr/local/other")
    }

    xtest("Reading current permissions") {
        // Given
        // TODO Use test generators to do exhaustive testing
    }

    xtest("Setting new permissions") {
        // Given
        val baseDirectory = TestDirectoryDefault.createUnsafe(
            listOf("usr", "local"),
        )
    }
})