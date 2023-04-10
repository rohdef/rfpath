package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permissions

class TestDirectoryDefault private constructor(
    path: List<String>
) : TestDirectory(path) {
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
                        ${formattedContent}
                    ]
                }
            }
        """.trimIndent()
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