package dk.rohdef.rfpath.test

import arrow.core.nonEmptyListOf
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.ResolveError
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.permissions.UserGroup
import dk.rohdef.rfpath.test.builders.root
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.element
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.exhaustive

class TestDirectoryTest : FunSpec({
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

        file shouldBe TestFileDefault.createUnsafe(nonEmptyListOf("usr", "local", "foo"))
        file2 shouldBe TestFileDefault.createUnsafe(nonEmptyListOf("usr", "local", "fish.sh"))
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

            TestFileDefault.createUnsafe(nonEmptyListOf("buffer")),
            TestFileDefault.createUnsafe(nonEmptyListOf("database.db")),
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
        foo shouldBe TestFileDefault.createUnsafe(nonEmptyListOf("usr", "local", "foo"))

        val other = otherResult.shouldBeLeft()
        other shouldBe ResolveError.ResourceNotFound("/usr/local/other")
    }


    test("Reading current permissions") {
        val all = Permission.values().toSet()
            .powerSet()
            .toList()
            .exhaustive()
        checkAll(all, all, all) { owner, group, other ->
            val directory = TestDirectoryDefault.createUnsafe(
                listOf(),
                Permissions(
                    owner,
                    group,
                    other,
                )
            )

            val permissions = directory.currentPermissions()

            permissions.owner shouldContainExactly owner
            permissions.group shouldContainExactly group
            permissions.other shouldContainExactly other
        }
    }

    test("Adding permissions") {
        val all = Permission.values().toSet()
            .powerSet()
            .toList()
            .exhaustive()

        checkAll(all, all, all, Exhaustive.enum<UserGroup>(), Exhaustive.enum<Permission>()) { owner, group, other, userGroup, permission ->
            val directory = TestDirectoryDefault.createUnsafe(
                listOf(),
                Permissions(
                    owner,
                    group,
                    other,
                )
            )

            directory.addPermission(userGroup, permission)
                .shouldBeRight()
            val permissions = directory.currentPermissions()

            when (userGroup) {
                UserGroup.OWNER -> {
                    permissions.owner shouldContainExactly (owner + permission)
                    permissions.group shouldContainExactly group
                    permissions.other shouldContainExactly other
                }
                UserGroup.GROUP -> {
                    permissions.owner shouldContainExactly owner
                    permissions.group shouldContainExactly (group + permission)
                    permissions.other shouldContainExactly other
                }
                UserGroup.OTHER -> {
                    permissions.owner shouldContainExactly owner
                    permissions.group shouldContainExactly group
                    permissions.other shouldContainExactly (other + permission)
                }
            }
        }
    }

    test("Setting new permissions") {
        val all = Permission.values().toSet()
            .powerSet()
            .toList()
            .exhaustive()
        val some = Permission.values().toSet()
            .powerSet()
            .let { Arb.element(it) }
        checkAll(
            all, all, all,
            some, some, some,
        ) { owner, group, other, newOwner, newGroup, newOther ->
            val directory = TestDirectoryDefault.createUnsafe(
                listOf(),
                Permissions(
                    owner,
                    group,
                    other,
                ),
            )

            directory.setPermissions(
                Permissions(
                    newOwner,
                    newGroup,
                    newOther,
                ),
            )
                .shouldBeRight()
            val permissions = directory.currentPermissions()

            permissions.owner shouldContainExactly newOwner
            permissions.group shouldContainExactly newGroup
            permissions.other shouldContainExactly newOther
        }
    }
})