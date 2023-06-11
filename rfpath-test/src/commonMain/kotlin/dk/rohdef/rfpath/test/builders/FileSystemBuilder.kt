package dk.rohdef.rfpath.test.builders

import dk.rohdef.rfpath.Path
import dk.rohdef.rfpath.test.*
import dk.rohdef.rfpath.test.SetExactlyOnce
import dk.rohdef.rfpath.utility.FileSystem

class FileSystemBuilder<DirectoryType : Path.Directory>(
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