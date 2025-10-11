package com.github.diydriller.databaseseeder.seeder

import com.github.diydriller.databaseseeder.domain.ColumnMetadata

interface DatabaseSeeder {
    fun connect()
    fun getColumns(table: String): List<ColumnMetadata>
    fun generateData(table: String, count: Int, mapping: Map<String, String>, log: (String) -> Unit)
    fun disconnect()
}