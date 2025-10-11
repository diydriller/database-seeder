package com.github.diydriller.databaseseeder.seeder

import com.github.diydriller.databaseseeder.domain.DatabaseType
import com.github.diydriller.databaseseeder.domain.ColumnMetadata

class SeederService {

    fun loadTableColumns(
        type: DatabaseType,
        url: String,
        user: String,
        password: String,
        table: String
    ): List<ColumnMetadata> {
        val seeder = DatabaseSeederFactory.create(type, url, user, password)
        return try {
            seeder.connect()
            seeder.getColumns(table)
        } finally {
            seeder.disconnect()
        }

    }

    fun generateWithMapping(
        type: DatabaseType,
        url: String,
        user: String,
        password: String,
        table: String,
        count: Int,
        mapping: Map<String, String>,
        logCallback: (String) -> Unit
    ) {
        val seeder = DatabaseSeederFactory.create(type, url, user, password)

        try {
            seeder.connect()
            logCallback("✅ Connected to ${type.name}")
            seeder.generateData(table, count, mapping, logCallback)
        } catch (e: Exception) {
            logCallback("❌ Failed: ${e.message}")
            throw e
        } finally {
            seeder.disconnect()
            logCallback("🔌 Connection closed")
        }
    }
}