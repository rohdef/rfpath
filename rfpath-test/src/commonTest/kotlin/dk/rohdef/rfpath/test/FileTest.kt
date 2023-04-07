package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FileTest : FunSpec({
    coroutineTestScope = true

    test("file has contents") {
        // Given
        val file = TestFile.createUnsafe("/file.txt")

        file.contents = """
            this is a test
        """.trimIndent()

        // When
        val textResult = file.readText()

        // Then
        val text = textResult
            .shouldBeRight()
        text shouldBe """
            this is a test
        """.trimIndent()
    }

    test("contents can be changed") {
        // Given
        val file = TestFile.createUnsafe("/file.txt")

        // When
        val textResult = file.write(
            """
            Multiple lines
            are expected
        """.trimIndent()
        )

        // Then
        val f = textResult
            .shouldBeRight()
        f.contents shouldBe """
            Multiple lines
            are expected
        """.trimIndent()
    }

    test("read permissions") {
        // Given
        val fileWithDefaultPermissions = TestFile.createUnsafe("/file.txt")
        val fileWithEmptyPermissions = TestFile.createUnsafe(
            "/file.txt",
            permissions = Permissions(
                owner = emptySet(),
                group = emptySet(),
                other = emptySet(),
            )
        )
        val fileWithMixedPermissions = TestFile.createUnsafe(
            "/file.txt",
            permissions = Permissions(
                owner = setOf(Permission.READ, Permission.EXECUTE),
                group = setOf(Permission.WRITE, Permission.EXECUTE),
                other = setOf(Permission.READ, Permission.WRITE),
            )
        )
        val fileWithMixedPermissions2 = TestFile.createUnsafe(
            "/file.txt",
            permissions = Permissions(
                owner = setOf(Permission.EXECUTE),
                group = setOf(Permission.READ),
                other = setOf(Permission.WRITE),
            )
        )

        // When
        val defaultPermissions = fileWithDefaultPermissions.currentPermissions()
        val emptyPermissions = fileWithEmptyPermissions.currentPermissions()
        val mixedPermissions = fileWithMixedPermissions.currentPermissions()
        val mixedPermissions2 = fileWithMixedPermissions2.currentPermissions()

        // Then
        defaultPermissions shouldBe Permissions(
            owner = setOf(Permission.READ, Permission.WRITE),
            group = setOf(Permission.READ, Permission.WRITE),
            other = emptySet(),
        )
        emptyPermissions shouldBe Permissions(
            owner = emptySet(),
            group = emptySet(),
            other = emptySet(),
        )
        mixedPermissions shouldBe Permissions(
            owner = setOf(Permission.READ, Permission.EXECUTE),
            group = setOf(Permission.WRITE, Permission.EXECUTE),
            other = setOf(Permission.READ, Permission.WRITE),
        )
        mixedPermissions2 shouldBe Permissions(
            owner = setOf(Permission.EXECUTE),
            group = setOf(Permission.READ),
            other = setOf(Permission.WRITE),
        )
    }

    test("change permission") {
        // Given
        val fileWithEmptyPermissions = TestFile.createUnsafe("/file.txt")
        val fileWithMixedPermissions = TestFile.createUnsafe("/file.txt")
        val fileWithMixedPermissions2 = TestFile.createUnsafe("/file.txt")

        // When
        val emptyPermissionsResult = fileWithEmptyPermissions
            .setPermissions(
                Permissions(
                    owner = emptySet(),
                    group = emptySet(),
                    other = emptySet(),
                )
            )
        val mixedPermissionsResult = fileWithMixedPermissions
            .setPermissions(
                Permissions(
                    owner = setOf(Permission.READ, Permission.EXECUTE),
                    group = setOf(Permission.WRITE, Permission.EXECUTE),
                    other = setOf(Permission.READ, Permission.WRITE),
                )
            )
        val mixedPermissions2Result = fileWithMixedPermissions2
            .setPermissions(
                Permissions(
                    owner = setOf(Permission.EXECUTE),
                    group = setOf(Permission.READ),
                    other = setOf(Permission.WRITE),
                )
            )

        // Then
        val emptyPermissions = emptyPermissionsResult.shouldBeRight()
        emptyPermissions.currentPermissions() shouldBe Permissions(
            owner = emptySet(),
            group = emptySet(),
            other = emptySet(),
        )
        val mixedPermissions = mixedPermissionsResult.shouldBeRight()
        mixedPermissions.currentPermissions() shouldBe Permissions(
            owner = setOf(Permission.READ, Permission.EXECUTE),
            group = setOf(Permission.WRITE, Permission.EXECUTE),
            other = setOf(Permission.READ, Permission.WRITE),
        )
        val mixedPermissions2 = mixedPermissions2Result.shouldBeRight()
        mixedPermissions2.currentPermissions() shouldBe Permissions(
            owner = setOf(Permission.EXECUTE),
            group = setOf(Permission.READ),
            other = setOf(Permission.WRITE),
        )
    }
})