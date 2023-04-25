package dk.rohdef.rfpath.test

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.permissions.Permissions

class TestDirectoryDefault private constructor(
    path: List<String>
) : TestDirectory<TestDirectoryDefault>(path) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is TestDirectoryDefault) {
            return absolutePath == other.absolutePath &&
                    contents == other.contents
        }

        return false
    }

    override fun hashCode(): Int {
        return absolutePath.hashCode()
    }

    override fun toString(): String {
        val formattedContent = contents
            .map { "${it.key}:${it.value}" }
            .joinToString(",\n")

        return """
            {
                "testDirectoryDefault": {
                    "absolutePath": "$absolutePath",
                    "permissions": {},
                    "contents": [
                        $formattedContent
                    ]
                }
            }
        """.trimIndent()
    }

    override suspend fun makeDirectory(directoryName: String): Either<MakeDirectoryError, TestDirectoryDefault> {
        if (contents.containsKey(directoryName)) {
            return MakeDirectoryError.DirectoryExists("$absolutePath/$directoryName").left()
        }

        val directory = createUnsafe(path + directoryName)
        contents[directoryName] = directory
        return directory.right()
    }

    companion object {
        fun createUnsafe(
            path: List<String>,
            permissions: Permissions = Permissions(
                owner = emptySet(),
                group = emptySet(),
                other = emptySet(),
            ),
        ): TestDirectoryDefault {
            return TestDirectoryDefault(path)
        }
    }
}