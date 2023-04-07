package dk.rohdef.rfpath.test

import arrow.core.Either
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.NewFileError
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permissions

class TestDirectoryDefault private constructor(
    override val absolutePath: String
) : TestDirectory(absolutePath) {
    companion object {
        fun createUnsafe(): TestDirectoryDefault {
            TODO()
        }
    }
}