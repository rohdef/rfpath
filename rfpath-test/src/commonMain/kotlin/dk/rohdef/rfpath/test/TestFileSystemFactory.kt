package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

fun root(configure: DirectoryContext.() -> Unit): Path.Directory {
    val rootDirectory = DirectoryContext()
    rootDirectory.configure()

    return rootDirectory.build()
}

class DirectoryContext {
    val directories = mutableMapOf<String, Path.Directory>()
    val files = mutableMapOf<String, Path.File>()

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
    )

    fun directory(name: String, configure: DirectoryContext.() -> Unit) {
        val directory = DirectoryContext()
        directory.configure()

        directories.put(name, directory.build())
    }

    fun file(name: String, configure: FileContext.() -> Unit) {
        val file = FileContext()
        file.configure()

        files.put(name, file.build())
    }

    internal fun build(): Path.Directory {
        return TODO()
    }
}

class FileContext {
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