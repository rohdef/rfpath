package dk.rohdef.rfpath.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun <Type> Collection<Type>.powerSet(): Set<Set<Type>> {
    return this.fold(setOf(emptySet())) { accumulator, element ->
        val n = accumulator.map { it + element }
        accumulator + n
    }
}

class PowersetTest : FunSpec({
    test("Empty set") {
        emptySet<Any>().powerSet()
            .shouldBe(
                setOf(
                    emptySet(),
                ),
            )
    }

    test("One element set") {
        setOf("foo").powerSet()
            .shouldBe(
                setOf(
                    emptySet(),
                    setOf("foo"),
                ),
            )
    }

    test("Two element set") {
        setOf(1, 2).powerSet()
            .shouldBe(
                setOf(
                    emptySet(),
                    setOf(1),
                    setOf(2),
                    setOf(1, 2),
                ),
            )
    }

    test("Three element set") {
        setOf("foo", "bar", "baz").powerSet()
            .shouldBe(
                setOf(
                    emptySet(),
                    setOf("foo"),
                    setOf("bar"),
                    setOf("baz"),
                    setOf("foo", "bar"),
                    setOf("foo", "baz"),
                    setOf("bar", "baz"),
                    setOf("foo", "bar", "baz"),
                ),
            )
    }
})