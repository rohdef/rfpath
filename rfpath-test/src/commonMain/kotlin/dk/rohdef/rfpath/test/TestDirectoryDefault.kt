package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permissions

class TestDirectoryDefault private constructor(
    override val absolutePath: String
) : TestDirectory(absolutePath) {
    companion object {
        fun createUnsafe(
            absolutePath: String,
            permissions: Permissions = Permissions(
                owner = emptySet(),
                group = emptySet(),
                other = emptySet(),
            ),
        ): TestDirectoryDefault {
            TODO()
        }
    }
}