package dk.rohdef.rfpath.test.builders

import arrow.core.toNonEmptyListOrNull
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

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