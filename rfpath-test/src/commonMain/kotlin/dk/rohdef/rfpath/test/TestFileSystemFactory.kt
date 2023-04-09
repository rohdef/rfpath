package dk.rohdef.rfpath.test

import arrow.core.getOrHandle
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

suspend fun root(configure: DirectoryContext.() -> Unit): Path.Directory {
    val rootDirectory = DirectoryContext(emptyList())
    rootDirectory.configure()

    return rootDirectory.build()
}

class DirectoryContext(
    val path: List<String>,
) {
    val directories = mutableMapOf<String, DirectoryContext>()
    val files = mutableMapOf<String, FileContext>()

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
    )

    fun directory(directoryName: String, configure: DirectoryContext.() -> Unit) {
        val directory = DirectoryContext(path + directoryName)
        directory.configure()

        directories.put(directoryName, directory)
    }

    fun file(fileName: String, configure: FileContext.() -> Unit) {
        val file = FileContext(fileName)
        file.configure()

        files.put(fileName, file)
    }

    suspend fun build(): TestDirectoryDefault {
        return build { TestDirectoryDefault.createUnsafe(path) }
    }

    private suspend fun build(builder: suspend (path: List<String>)->TestDirectoryDefault): TestDirectoryDefault {
        val directory = builder(path)

        directories.forEach { subDirectory ->
            subDirectory.value.build {
                directory.makeDirectory(subDirectory.key)
                    .getOrHandle { throw RuntimeException("This is not possible in the test structure: ${it}") }
            }
        }

        return directory
    }
}

class FileContext(
    val fileName: String,
) {
    var contents = ""

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ),
    )

    internal fun build(): Path.File {
        return TODO()
    }
}