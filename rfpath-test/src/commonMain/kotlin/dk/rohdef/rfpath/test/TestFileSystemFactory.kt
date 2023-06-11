package dk.rohdef.rfpath.test

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.toNonEmptyListOrNull
import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.utility.FileSystem

suspend fun <Accumulator : Path.Directory> fileSystem(
    base: Accumulator,
    directoryBuilder: DirectoryBuilder<Accumulator>,
    fileBuilder: FileBuilder<Accumulator>,
    configure: suspend FileSystemRoot<Accumulator>.() -> Unit,
): FileSystem {
    val fs = FileSystemRoot(
        base,
        directoryBuilder,
        fileBuilder,
    )
    fs.configure()

    return fs.build()
}

suspend fun fileSystem(configure: suspend FileSystemRoot<TestDirectoryDefault>.() -> Unit): FileSystem {
    return fileSystem(
        TestDirectoryDefault.createUnsafe(emptyList()),
        defaultDirectoryBuilder,
        defaultFileBuilder,
        configure,
    )
}

val defaultDirectoryBuilder: DirectoryBuilder<TestDirectoryDefault> = { accumulator, directoryContext ->
    accumulator.makeDirectory(directoryContext.path.last())
        .getOrElse { throw IllegalStateException(it.toString()) }
}

val defaultFileBuilder: FileBuilder<TestDirectoryDefault> = { accumulator, fileContext ->
    val file = accumulator
        .makeFile(fileContext.fileName)
        .getOrElse { throw IllegalStateException(it.toString()) }

    file.contents = fileContext.contents
    file.permissions = fileContext.permissions
}

class FileSystemRoot<DirectoryType : Path.Directory>(
    private val base: DirectoryType,
    private val directoryBuilder: DirectoryBuilder<DirectoryType>,
    private val fileBuilder: FileBuilder<DirectoryType>,
) {
    private val application = SetExactlyOnce<DirectoryType>()
    private var workDirectory = SetExactlyOnce<DirectoryType>()
    private var temporary = SetExactlyOnce<DirectoryType>()
    private lateinit var root: DirectoryType

    fun application(me: DirectoryContext<DirectoryType>) = me.addPostBuilder(application::setValue)
    fun workDirectory(me: DirectoryContext<DirectoryType>) = me.addPostBuilder(workDirectory::setValue)
    fun temporary(me: DirectoryContext<DirectoryType>) = me.addPostBuilder(temporary::setValue)

    suspend fun root(
        configure: DirectoryContext<DirectoryType>.() -> Unit,
    ) {
        root = root(
            base,
            directoryBuilder,
            fileBuilder,
            configure,
        )
    }

    fun build(): FileSystem {
        return TestFileSystem(
            root,
            application.value,
            workDirectory.value,
            temporary.value,
        )
    }
}

// TODO this is not thread safe
private data class SetExactlyOnce<T>(
    private var _value: T? = null
) {
    val value: T
        get() {
            return _value ?: throw IllegalStateException("Value must be set")
        }

    fun setValue(value: T) {
        if (_value == null) {
            _value = value
        } else {
            throw IllegalStateException("Value has already been set")
        }
    }
}


// TODO fix variance
typealias DirectoryBuilder<Accumulator> = suspend (accumulator: Accumulator, newDirectory: DirectoryContext<Accumulator>) -> Accumulator
typealias FileBuilder<Accumulator> = suspend (accumulator: Accumulator, newFile: FileContext) -> Unit

suspend fun root(
    configure: DirectoryContext<TestDirectoryDefault>.() -> Unit,
): TestDirectoryDefault {
    return root(
        TestDirectoryDefault.createUnsafe(emptyList()),
        defaultDirectoryBuilder,
        defaultFileBuilder,
        configure,
    )
}

suspend fun <Accumulator> root(
    base: Accumulator,
    directoryBuilder: DirectoryBuilder<Accumulator>,
    fileBuilder: FileBuilder<Accumulator>,
    configure: DirectoryContext<Accumulator>.() -> Unit,
): Accumulator {
    val rootDirectory = DirectoryContext(
        emptyList(),
        directoryBuilder,
        fileBuilder,
    )
    rootDirectory.configure()

    return rootDirectory.build(base)
}

class DirectoryContext<Accumulator>(
    val path: List<String>,
    val directoryBuilder: DirectoryBuilder<Accumulator>,
    val fileBuilder: FileBuilder<Accumulator>,
) {
    val directories = mutableMapOf<String, DirectoryContext<Accumulator>>()
    val files = mutableMapOf<String, FileContext>()

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
    )

    private val postBuilders: MutableList<(Accumulator) -> Unit> = mutableListOf()

    fun addPostBuilder(postBuilder: (Accumulator) -> Unit) {
        postBuilders.add(postBuilder)
    }

    fun directory(directoryName: String, configure: DirectoryContext<Accumulator>.() -> Unit) {
        if (directoryName.isEmpty()) throw IllegalArgumentException("Directory name cannot be empty")

        val directory = DirectoryContext(
            path + directoryName,
            directoryBuilder,
            fileBuilder,
        )
        directory.configure()

        directories[directoryName] = directory
    }

    fun file(fileName: String, configure: FileContext.() -> Unit) {
        if (fileName.isEmpty()) throw IllegalArgumentException("File name cannot be empty")

        val file = FileContext((path + fileName).toNonEmptyListOrNull()!!)
        file.configure()

        files[fileName] = file
    }

    suspend fun build(accumulator: Accumulator): Accumulator {
        directories.forEach {
            it.value.build(directoryBuilder(accumulator, it.value))
        }

        files.forEach {
            fileBuilder(accumulator, it.value)
        }

        postBuilders.forEach { it(accumulator) }

        return accumulator
    }
}

class FileContext(
    val path: NonEmptyList<String>,
) {
    val fileName = path.last()

    var contents = ""

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ, Permission.WRITE),
        setOf(Permission.READ),
    )
}