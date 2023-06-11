package dk.rohdef.rfpath.test.builders

// TODO fix variance
typealias DirectoryBuilder<Accumulator> = suspend (accumulator: Accumulator, newDirectory: DirectoryContext<Accumulator>) -> Accumulator
typealias FileBuilder<Accumulator> = suspend (accumulator: Accumulator, newFile: FileContext) -> Unit