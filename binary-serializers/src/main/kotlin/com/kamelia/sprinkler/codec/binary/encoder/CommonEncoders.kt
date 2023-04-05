@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.composer.composedEncoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


@JvmOverloads
fun UUIDEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<UUID> =
    composedEncoder<UUID>()
        .encodeWith(longEncoder, UUID::getMostSignificantBits)
        .encodeWith(longEncoder, UUID::getLeastSignificantBits)
        .build()

@JvmOverloads
fun UUIDStringEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<UUID> =
    stringEncoder.withMappedInput(UUID::toString)

fun <T, U> PairEncoder(firstEncoder: Encoder<T>, secondEncoder: Encoder<U>): Encoder<Pair<T, U>> =
    composedEncoder<Pair<T, U>>()
        .encodeWith(firstEncoder, Pair<T, U>::first)
        .encodeWith(secondEncoder, Pair<T, U>::second)
        .build()

@JvmOverloads
fun InstantEncoder(
    secondNanoRepresentation: Boolean = false,
    longEncoder: Encoder<Long> = LongEncoder(),
): Encoder<Instant> = if (secondNanoRepresentation) {
    composedEncoder<Instant>()
        .encodeWith(longEncoder, Instant::getEpochSecond)
        .encodeWith(longEncoder) { nano.toLong() }
        .build()
} else {
    longEncoder.withMappedInput(Instant::toEpochMilli)
}

@JvmOverloads
fun LocalTimeEncoder(
    decodeNanos: Boolean = false,
    intEncoder: Encoder<Int> = IntEncoder(),
): Encoder<LocalTime> {
    val composer = composedEncoder<LocalTime>()
        .encodeWith(intEncoder, LocalTime::getHour)
        .encodeWith(intEncoder, LocalTime::getMinute)
        .encodeWith(intEncoder, LocalTime::getSecond)
    if (decodeNanos) {
        composer.encodeWith(intEncoder, LocalTime::getNano)
    }
    return composer.build()
}

fun LocalDateEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<LocalDate> =
    composedEncoder<LocalDate>()
        .encodeWith(intEncoder, LocalDate::getYear)
        .encodeWith(intEncoder, LocalDate::getMonthValue)
        .encodeWith(intEncoder, LocalDate::getDayOfMonth)
        .build()

fun DateEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<Date> = longEncoder.withMappedInput(Date::getTime)

fun ZoneIdEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<ZoneId> =
    stringEncoder.withMappedInput(ZoneId::getId)

fun ZonedDateTimeEncoder(
    zoneIdEncoder: Encoder<ZoneId> = ZoneIdEncoder(),
    instantEncoder: Encoder<Instant> = InstantEncoder(),
): Encoder<ZonedDateTime> =
    composedEncoder<ZonedDateTime>()
        .encodeWith(instantEncoder, ZonedDateTime::toInstant)
        .encodeWith(zoneIdEncoder, ZonedDateTime::getZone)
        .build()
