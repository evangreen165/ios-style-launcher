package com.olivergreen.ioslauncher

import com.olivergreen.ioslauncher.data.mergePinnedFirst
import com.olivergreen.ioslauncher.data.pageCountFor
import com.olivergreen.ioslauncher.data.sortAppsByLabel
import java.text.Collator
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class AppLayoutTest {

    @Test
    fun `paging caps at 24 cells per page`() {
        assertEquals(1, pageCountFor(0))
        assertEquals(1, pageCountFor(1))
        assertEquals(1, pageCountFor(24))
        assertEquals(2, pageCountFor(25))
        assertEquals(2, pageCountFor(48))
        assertEquals(3, pageCountFor(49))
        assertEquals(5, pageCountFor(120))
    }

    @Test
    fun `pinned apps come first preserving their order`() {
        val apps = listOf(testEntry("Бета", "b"), testEntry("Альфа", "a"), testEntry("Гамма", "g"))
        val result = mergePinnedFirst(apps, listOf("g", "b"))
        assertEquals(listOf("g", "b", "a"), result.map { it.packageName })
    }

    @Test
    fun `missing and duplicate pinned entries are skipped without duplicates`() {
        val apps = listOf(testEntry("А", "a"), testEntry("Б", "b"))
        val result = mergePinnedFirst(apps, listOf("a", "a", "zzz"))
        assertEquals(listOf("a", "b"), result.map { it.packageName })
    }

    @Test
    fun `empty app list returns empty regardless of pinned`() {
        assertEquals(emptyList<Any>(), mergePinnedFirst(emptyList(), listOf("x")))
    }

    @Test
    fun `remaining apps keep alphabetical order after pinned`() {
        val apps = listOf(
            testEntry("Чай", "c"),
            testEntry("Апельсин", "a"),
            testEntry("Банан", "b"),
        )
        val result = mergePinnedFirst(apps, listOf("c"))
        assertEquals(listOf("c", "a", "b"), result.map { it.packageName })
    }

    @Test
    fun `sorts labels with russian collation`() {
        val collator = Collator.getInstance(Locale("ru", "RU"))
        val apps = listOf(testEntry("Яндекс", "y"), testEntry("Банк", "b"), testEntry("Альфа", "a"))
        val result = sortAppsByLabel(apps, collator)
        assertEquals(listOf("Альфа", "Банк", "Яндекс"), result.map { it.label })
    }

    @Test
    fun `empty collator input returns empty`() {
        val collator = Collator.getInstance(Locale("ru", "RU"))
        assertEquals(emptyList<Any>(), sortAppsByLabel(emptyList(), collator))
    }
}
