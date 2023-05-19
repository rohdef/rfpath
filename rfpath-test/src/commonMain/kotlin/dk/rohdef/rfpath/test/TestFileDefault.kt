package dk.rohdef.rfpath.test

import arrow.core.NonEmptyList
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

class TestFileDefault private constructor(
    path: NonEmptyList<String>,
    permissions: Permissions,
) : TestFile<TestFileDefault>(path, permissions) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is TestFileDefault) {
            return absolutePath == other.absolutePath
                    && permissions == other.permissions
                    && contents == other.contents
        }

        return false
    }

    override fun hashCode(): Int {
        return absolutePath.hashCode() * 7 +
                permissions.hashCode() * 13 +
                contents.hashCode() * 17
    }

    override fun toString(): String {
        return """
            {
                "TestFileDefault": {
                    "absolutePath": "$absolutePath",
                    "permissions": $permissions,
                    "contents": "$contents"
                }
            }
        """.trimIndent()
    }

    companion object {
        fun createUnsafe(
            path: NonEmptyList<String>,
            permissions: Permissions = Permissions(
                owner = setOf(Permission.READ, Permission.WRITE),
                group = setOf(Permission.READ, Permission.WRITE),
                other = setOf(Permission.READ),
            )
        ): TestFileDefault {
            return TestFileDefault(path, permissions)
        }
    }
}