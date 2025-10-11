package com.github.diydriller.databaseseeder.faker

import com.github.diydriller.databaseseeder.domain.DatabaseDataType

object FakerTypeRegistry {
    private val dbTypeToCategory = mapOf(
        "varchar" to DatabaseDataType.STRING,
        "text" to DatabaseDataType.STRING,

        "int" to DatabaseDataType.NUMBER,
        "bigint" to DatabaseDataType.NUMBER,

        "timestamp" to DatabaseDataType.DATETIME,

        "boolean" to DatabaseDataType.BOOLEAN
    )

    private val fakerTypeByCategory = mapOf(
        DatabaseDataType.STRING to arrayOf("name", "email", "phone", "address"),
        DatabaseDataType.NUMBER to arrayOf("int32", "int64", "positive int32", "positive int64"),
        DatabaseDataType.BOOLEAN to arrayOf("boolean"),
        DatabaseDataType.DATETIME to arrayOf("timestamp"),
        DatabaseDataType.UNKNOWN to arrayOf("none")
    )

    fun getFakerTypes(dbType: String): Array<String> {
        val category = dbTypeToCategory.entries
            .firstOrNull { dbType.lowercase().contains(it.key) }
            ?.value ?: DatabaseDataType.UNKNOWN

        return fakerTypeByCategory[category] ?: arrayOf("none")
    }
}