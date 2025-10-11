package com.github.diydriller.databaseseeder.util

import java.sql.Timestamp
import java.time.LocalDateTime

object TimeUtil {
    fun toTimestamp(localDateTime: LocalDateTime): Timestamp =
        Timestamp.valueOf(localDateTime)

    fun fromTimestamp(timestamp: Timestamp): LocalDateTime =
        timestamp.toLocalDateTime()
}