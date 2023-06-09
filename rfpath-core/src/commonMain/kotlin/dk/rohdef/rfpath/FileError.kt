package dk.rohdef.rfpath

sealed interface FileError : PathError<Path.File> {
    object NotAFile : FileError
}

sealed interface FileInstance : FileError {
    data class EntityIsADirectory(val path: String) : FileInstance
    data class EntityIsNonExisting(val path: String) : FileInstance
}