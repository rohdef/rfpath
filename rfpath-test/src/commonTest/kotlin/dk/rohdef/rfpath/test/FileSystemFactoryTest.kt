package dk.rohdef.rfpath.test

import dk.rohdef.rfpath.permissions.Permission
import dk.rohdef.rfpath.permissions.Permissions
import dk.rohdef.rfpath.test.builders.fileSystem
import dk.rohdef.rfpath.test.builders.root
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class FileSystemFactoryTest : FunSpec({
    coroutineTestScope = true

    test("can read complex file system") {
        val fileSystem = root {
            directory("bin") {
            }

            directory("etc") {
            }

            directory("usr") {
                // // TODO: 08/04/2023 rohdef - implement when directory permissions are available
//                permissions = permissions.copy(other = emptySet())

                directory("local") {
                    directory("bin") {
                        file("laursen") {
                            contents = """
                                #!/bin/bash
                                
                                echo "I am a fish!"
                            """.trimIndent()

                            permissions = Permissions(
                                owner = setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
                                group = setOf(Permission.READ, Permission.EXECUTE),
                                other = setOf(Permission.READ, Permission.EXECUTE),
                            )
                        }
                    }
                }
            }
        }

        val expected = TestDirectoryDefault.createUnsafe(emptyList())
        expected.makeDirectory("bin")
            .shouldBeRight()

        expected.makeDirectory("etc")
            .shouldBeRight()

        val usr = expected.makeDirectory("usr")
            .shouldBeRight()
        val usrLocal = usr.makeDirectory("local")
            .shouldBeRight()
        val usrLocalBin = usrLocal.makeDirectory("bin")
            .shouldBeRight()

        val laursenFile = usrLocalBin.makeFile("laursen")
            .shouldBeRight()
        laursenFile.permissions = Permissions(
            owner = setOf(Permission.READ, Permission.WRITE, Permission.EXECUTE),
            group = setOf(Permission.READ, Permission.EXECUTE),
            other = setOf(Permission.READ, Permission.EXECUTE),
        )
        laursenFile.contents = """
            #!/bin/bash

            echo "I am a fish!"
        """.trimIndent()

        fileSystem
            .shouldBe(expected)
    }

    test("handling of system directories") {
        val filesystem = fileSystem {
            root {
                directory("bin") {
                    directory("gourmet") {
                        application(this)
                    }
                }

                directory("home") {
                    directory("camilla") {
                        workDirectory(this)
                    }
                }

                directory("tmp") {
                    temporary(this)

                    directory("local") { }
                }
            }
        }

        val root = filesystem.root()
            .shouldBeRight()
        root.directoryName shouldBe "/"
        root.absolutePath shouldBe "/"

        val app = filesystem.applicationDirectory()
            .shouldBeRight()
        app.directoryName shouldBe "gourmet"
        app.absolutePath shouldBe "/bin/gourmet"

        val work = filesystem.workDirectory()
            .shouldBeRight()
        work.directoryName shouldBe "camilla"
        work.absolutePath shouldBe "/home/camilla"

        val tmpFile = filesystem.createTemporaryFile()
            .shouldBeRight()
        tmpFile.absolutePath shouldStartWith "/tmp/"
    }
})