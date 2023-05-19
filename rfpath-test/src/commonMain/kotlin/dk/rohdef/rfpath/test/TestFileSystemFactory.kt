package dk.rohdef.rfpath.test

import arrow.core.*
import arrow.core.continuations.either
import dk.rohdef.rfpath.DirectoryError
import dk.rohdef.rfpath.MakeDirectoryError
import dk.rohdef.rfpath.MakeFileError
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

// TODO fix variance
typealias DirectoryBuilder<Accumulator> = suspend (accumulator: Accumulator, newDirectory: DirectoryContext<Accumulator>) -> Accumulator
typealias FileBuilder<Accumulator> = suspend (accumulator: Accumulator, newFile: FileContext) -> Accumulator

suspend fun root(
    configure: DirectoryContext<Either<DirectoryError, TestDirectoryDefault>>.() -> Unit,
): Either<DirectoryError, TestDirectoryDefault> {
    val dbuilder: DirectoryBuilder<Either<DirectoryError, TestDirectoryDefault>>  =
        { accumulator, directoryContext -> accumulator.flatMap { it.makeDirectory(directoryContext.path.last()) } }

    val fbuilder: suspend (accumulator: Either<DirectoryError, TestDirectoryDefault>, fileContext: FileContext) -> Unit = { accumulator, fileContext ->
        either {
            val file = accumulator.bind()
                .makeFile(fileContext.fileName)
                .bind()

            file.contents = fileContext.contents
            file.permissions = fileContext.permissions
        }
    }

    return root(
        // TODO fix typing for either
        TestDirectoryDefault.createUnsafe(emptyList()).right(),
        dbuilder,
        fbuilder,
        configure,
    )
}

suspend fun <Accumulator> root(
    base: Accumulator,
    directoryBuilder: DirectoryBuilder<Accumulator>,
    fileBuilder: suspend (accumulator: Accumulator, newFile: FileContext) -> Unit,
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
    val fileBuilder: suspend (accumulator: Accumulator, newFile: FileContext) -> Unit,
) {
    val directories = mutableMapOf<String, DirectoryContext<Accumulator>>()
    val files = mutableMapOf<String, FileContext>()

    var permissions = Permissions(
        setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
        setOf(Permission.READ, Permission.EXECUTE),
    )

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