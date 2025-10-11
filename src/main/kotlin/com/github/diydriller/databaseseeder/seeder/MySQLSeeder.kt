package com.github.diydriller.databaseseeder.seeder

import com.github.diydriller.databaseseeder.domain.ColumnMetadata
import com.github.diydriller.databaseseeder.faker.FakerGenerator.generate
import java.sql.Connection
import java.sql.DriverManager

class MySQLSeeder(
    private val url: String,
    private val username: String,
    private val password: String
) : DatabaseSeeder {

    private var connection: Connection? = null

    override fun connect() {
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection = DriverManager.getConnection(url, username, password)
    }

    override fun getColumns(table: String): List<ColumnMetadata> {
        val conn = connection ?: throw IllegalStateException("Not connected. Call connect() first.")
        val columns = mutableListOf<ColumnMetadata>()

        val dbName = conn.catalog
        val query = """
                SELECT COLUMN_NAME, DATA_TYPE, EXTRA, COLUMN_DEFAULT
                FROM information_schema.columns
                WHERE table_schema = ? AND table_name = ?
                ORDER BY ORDINAL_POSITION
            """.trimIndent()

        conn.prepareStatement(query).use { stmt ->
            stmt.setString(1, dbName)
            stmt.setString(2, table)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                val name = rs.getString("COLUMN_NAME")
                val type = rs.getString("DATA_TYPE")
                val extra = rs.getString("EXTRA") ?: ""
                val def = rs.getString("COLUMN_DEFAULT")
                if (!extra.contains("auto_increment") && def.isNullOrEmpty()) {
                    columns.add(ColumnMetadata(name, type))
                }
            }
        }
        return columns
    }

    override fun disconnect() {
        connection?.close()
    }

    override fun generateData(table: String, count: Int, mapping: Map<String, String>, log: (String) -> Unit) {
        val conn = connection ?: throw IllegalStateException("Not connected")
        val columns = mapping.keys.toList()
        val sql = "INSERT INTO $table (${columns.joinToString(",")}) VALUES (${columns.joinToString(",") { "?" }})"

        conn.prepareStatement(sql).use { stmt ->
            repeat(count) { row ->
                columns.forEachIndexed { i, col ->
                    val type = mapping[col]!!
                    val value = generate(type)
                    stmt.setObject(i + 1, value)
                }
                stmt.addBatch()
                if ((row + 1) % 100 == 0) stmt.executeBatch()
            }
            stmt.executeBatch()
        }
        log("✅ Inserted $count rows into $table")
    }
}