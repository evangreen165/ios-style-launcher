package com.olivergreen.ioslauncher

import com.olivergreen.ioslauncher.ui.filterApps
import com.olivergreen.ioslauncher.ui.normalizeForSearch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFilterTest {

    @Test
    fun `empty query returns all apps`() {
        val apps = listOf(testEntry("Альфа", "a"), testEntry("Бета", "b"))
        assertEquals(2, filterApps(apps, "").size)
        assertEquals(2, filterApps(apps, "   ").size)
    }

    @Test
    fun `case insensitive substring match`() {
        val apps = listOf(testEntry("YouTube", "y"), testEntry("Gmail", "g"))
        assertEquals(listOf("YouTube"), filterApps(apps, "youtube").map { it.label })
        assertTrue(filterApps(apps, "tube").isNotEmpty())
        assertEquals(listOf("Gmail"), filterApps(apps, "MAIL").map { it.label })
    }

    @Test
    fun `diacritic insensitive match`() {
        val apps = listOf(testEntry("Съёмка", "s"))
        assertTrue(filterApps(apps, "съемка").isNotEmpty())
        assertTrue(filterApps(apps, "СЪЁМКА").isNotEmpty())
    }

    @Test
    fun `no match returns empty`() {
        val apps = listOf(testEntry("Альфа", "a"))
        assertEquals(0, filterApps(apps, "такого нет").size)
    }

    @Test
    fun `normalize strips diacritics and lowercases`() {
        assertEquals("съемка", normalizeForSearch("Съёмка"))
        assertEquals("cafe", normalizeForSearch("Café"))
        assertEquals("naive", normalizeForSearch("Naïve"))
    }
}
