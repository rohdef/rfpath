package dk.rohdef.rfpath.test.builders

import arrow.core.getOrElse
import dk.rohdef.rfpath.test.TestDirectoryDefault

object DefaultBuilders {
    val directory: DirectoryBuilder<TestDirectoryDefault> = { accumulator, directoryContext ->
        accumulator.makeDirectory(directoryContext.path.last())
            .getOrElse { throw IllegalStateException(it.toString()) }
    }

    val file: FileBuilder<TestDirectoryDefault> = { accumulator, fileContext ->
        val file = accumulator
            .makeFile(fileContext.fileName)
            .getOrElse { throw IllegalStateException(it.toString()) }

        file.contents = fileContext.contents
        file.permissions = fileContext.permissions
    }
}