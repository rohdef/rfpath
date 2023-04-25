package dk.rohdef.rfpath.test

import arrow.core.getOrHandle
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

suspend fun root(configure: DirectoryContext.() -> Unit): Path.Directory {
    val builder: suspend (path: List<String>) -> TestDirectory = { TestDirectoryDefault.createUnsafe(it) }
    val rootDirectory = DirectoryContext(emptyList(), builder)
    rootDirectory.configure()

    return rootDirectory.build()
}

class DirectoryContext(
    val path: List<String>,
    var builder: suspend (path: List<String>) -> TestDirectory,
) {
    val directories = mutableMapOf<String, DirectoryContext>()
    val files = mutableMapOf<String, FileContext>()

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
    )

    fun directory(directoryName: String, configure: DirectoryContext.() -> Unit) {
        val directory = DirectoryContext(path + directoryName, builder)
        directory.configure()

        directories.put(directoryName, directory)
    }

    fun file(fileName: String, configure: FileContext.() -> Unit) {
        val file = FileContext(path + fileName)
        file.configure()

        files.put(fileName, file)
    }

    suspend fun build(): TestDirectory {
        return build(builder)
    }

    private suspend fun build(builder: suspend (path: List<String>)->TestDirectory): TestDirectory {
        val directory = builder(path)

        directories.forEach { subDirectory ->
            subDirectory.value.build {
                directory.makeDirectory(subDirectory.key)
                    .getOrHandle { throw RuntimeException("This is not possible in the test structure: ${it}") }
            }
        }
        files.forEach { subFile ->
            subFile.value.build {
                directory.makeFile(subFile.key)
                    .getOrHandle { throw RuntimeException("This is not possible in the test structure: ${it}") }
            }
        }

        return directory
    }
}

class FileContext(
    val path: List<String>,
) {
    var contents = ""

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ),
    )

    internal suspend fun build(builder: suspend (path: List<String>)->TestFile<*>): TestFile<*> {
        val file = builder(path)

        file.contents = contents
        file.permissions = permissions

        return file
    }
}