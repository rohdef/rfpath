package dk.rohdef.rfpath

import arrow.core.Either
import dk.rohdef.rfpath.permissions.Permissions
import kotlin.test.Test

class PathTest {
    // TODO: 10/11/2022 rohdef - test default methods
    class `Add permissions` {
        @Test
        fun `add permission`() {

        }

        @Test
        fun `permission already exists`() {

        }

        @Test
        fun `no permission to change permissions`() {

        }
    }

    private class DefaultPath : Path.File {
        override val absolutePath: String = "absolute path"

        override suspend fun setPermissions(permissions: Permissions): Either<DirectoryError, Path.File> {
            TODO("not implemented")
        }

        override suspend fun currentPermissions(): Permissions {
            TODO("not implemented")
        }

        override suspend fun write(text: String): Either<FileError, Path.File> {
            TODO("not implemented")
        }
    }
}