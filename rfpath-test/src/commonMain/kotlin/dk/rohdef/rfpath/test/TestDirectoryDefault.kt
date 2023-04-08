package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permissions

class TestDirectoryDefault private constructor(
    override val absolutePath: String
) : TestDirectory(absolutePath) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this::class.isInstance(other)) return false

        other as TestDirectoryDefault

        return absolutePath == other.absolutePath
    }

    override fun hashCode(): Int {
        return absolutePath.hashCode()
    }

    override fun toString(): String {
        return """
            {
                "testDirectoryDefault": {
                    "absolutePath": "$absolutePath",
                    "permissions": {},
                    "contents": [
                        ${contents
                            .map { it.toString() }
                            .joinToString { ",\n                        " }
                        }
                    ]
                }
            }
        """.trimIndent()
    }

    companion object {
        fun createUnsafe(
            absolutePath: String,
            permissions: Permissions = Permissions(
                owner = emptySet(),
                group = emptySet(),
                other = emptySet(),
            ),
        ): TestDirectoryDefault {
            return TestDirectoryDefault(absolutePath)
        }
    }
}