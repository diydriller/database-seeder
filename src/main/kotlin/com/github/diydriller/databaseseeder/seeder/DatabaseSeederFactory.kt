package com.github.diydriller.databaseseeder.seeder

import com.github.diydriller.databaseseeder.domain.DatabaseType

object DatabaseSeederFactory {
    fun create(type: DatabaseType, url: String, user: String, password: String): DatabaseSeeder {
        return when (type) {
            DatabaseType.MYSQL -> MySQLSeeder(url, user, password)
        }
    }
}
