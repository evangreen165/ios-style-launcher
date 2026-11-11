package com.olivergreen.ioslauncher.data

import java.text.Collator

const val HOME_COLUMNS = 4
const val HOME_ROWS = 6
const val HOME_CELLS_PER_PAGE = HOME_COLUMNS * HOME_ROWS

fun pageCountFor(appCount: Int): Int =
    if (appCount <= 0) 1 else (appCount + HOME_CELLS_PER_PAGE - 1) / HOME_CELLS_PER_PAGE

fun mergePinnedFirst(all: List<AppEntry>, pinned: List<String>): List<AppEntry> {
    if (all.isEmpty()) return all
    val byPackage = all.associateBy { it.packageName }
    val pinnedValid = mutableListOf<AppEntry>()
    val seen = mutableSetOf<String>()
    for (pkg in pinned) {
        val entry = byPackage[pkg]
        if (entry != null && seen.add(pkg)) {
            pinnedValid.add(entry)
        }
    }
    return pinnedValid + all.filter { it.packageName !in seen }
}

fun sortAppsByLabel(apps: List<AppEntry>, collator: Collator): List<AppEntry> =
    apps.sortedWith(compareBy(collator) { it.label })
