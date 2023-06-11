package dk.rohdef.rfpath.test.builders

import dk.rohdef.rfpath.test.TestDirectoryDefault

suspend fun root(
    configure: DirectoryContext<TestDirectoryDefault>.() -> Unit,
): TestDirectoryDefault {
    return root(
        TestDirectoryDefault.createUnsafe(emptyList()),
        DefaultBuilders.directory,
        DefaultBuilders.file,
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