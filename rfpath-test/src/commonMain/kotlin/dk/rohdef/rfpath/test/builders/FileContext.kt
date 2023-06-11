package dk.rohdef.rfpath.test.builders

import arrow.core.NonEmptyList
import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions

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