package com.github.diydriller.databaseseeder.faker

import com.github.diydriller.databaseseeder.util.TimeUtil.toTimestamp
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime

object FakerGenerator {
    private val faker = Faker()

    fun generate(type: String): Any? = when (type) {
        "name" -> faker.name.name()
        "email" -> faker.internet.email()
        "phone" -> faker.phoneNumber.phoneNumber()
        "address" -> faker.address.fullAddress()
        "timestamp" -> {
            val secondsRange = 60L * 60 * 24 * 365
            toTimestamp(LocalDateTime.now().minusSeconds(faker.random.nextLong(secondsRange)))
        }
        "int64" -> faker.random.nextLong()
        "int32" -> faker.random.nextInt()
        "positive int64" -> faker.random.nextLong(Int.MAX_VALUE.toLong())
        "positive int32" -> faker.random.nextInt(Int.MAX_VALUE)
        "boolean" -> faker.random.nextBoolean()
        else -> null
    }
}