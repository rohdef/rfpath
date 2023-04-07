package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

class TestFileDefault private constructor(
    override val absolutePath: String,
    permissions: Permissions,
) : TestFile<TestFileDefault>(absolutePath, permissions) {
    companion object {
        fun createUnsafe(
            absolutePath: String,
            permissions: Permissions = Permissions(
                owner = setOf(Permission.READ, Permission.WRITE),
                group = setOf(Permission.READ, Permission.WRITE),
                other = emptySet(),
            )
        ): TestFileDefault {
            return TestFileDefault(absolutePath, permissions)
        }
    }
}