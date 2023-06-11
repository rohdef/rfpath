package dk.rohdef.rfpath.test.builders

import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.test.TestDirectoryDefault
import dk.rohdef.rfpath.utility.FileSystem

suspend fun <Accumulator : Path.Directory> fileSystem(
    base: Accumulator,
    directoryBuilder: DirectoryBuilder<Accumulator>,
    fileBuilder: FileBuilder<Accumulator>,
    configure: suspend FileSystemBuilder<Accumulator>.() -> Unit,
): FileSystem {
    val fs = FileSystemBuilder(
        base,
        directoryBuilder,
        fileBuilder,
    )
    fs.configure()

    return fs.build()
}

suspend fun fileSystem(configure: suspend FileSystemBuilder<TestDirectoryDefault>.() -> Unit): FileSystem {
    return fileSystem(
        TestDirectoryDefault.createUnsafe(emptyList()),
        DefaultBuilders.directory,
        DefaultBuilders.file,
        configure,
    )
}