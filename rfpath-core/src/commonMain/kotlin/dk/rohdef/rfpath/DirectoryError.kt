package dk.rohdef.rfpath

sealed interface DirectoryError : PathError<Path.Directory>

sealed interface ResolveError : DirectoryError {
    data class ResourceNotFound(val path: String): ResolveError

    object BadResourceResolved: ResolveError
}

sealed interface MakeDirectoryError : DirectoryError {
    data class DirectoryExists(val path: String) : MakeDirectoryError
}

sealed interface MakeFileError : DirectoryError {
    data class FileExists(val path: String) : MakeFileError
}

sealed interface DirectoryInstance : DirectoryError {
    data class EntityIsAFile(val path: String) : DirectoryInstance
    data class EntityIsNonExisting(val path: String) : DirectoryInstance
}