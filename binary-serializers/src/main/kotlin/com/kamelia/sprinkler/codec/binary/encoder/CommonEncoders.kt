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
    composedEncoder {
        encode(it.mostSignificantBits, longEncoder)
        encode(it.leastSignificantBits, longEncoder)
    }

@JvmOverloads
fun UUIDStringEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<UUID> =
    stringEncoder.withMappedInput(UUID::toString)

fun <T, U> PairEncoder(firstEncoder: Encoder<T>, secondEncoder: Encoder<U>): Encoder<Pair<T, U>> =
    composedEncoder {
        encode(it.first, firstEncoder)
        encode(it.second, secondEncoder)
    }

@JvmOverloads
fun InstantEncoder(
    secondNanoRepresentation: Boolean = false,
    longEncoder: Encoder<Long> = LongEncoder(),
): Encoder<Instant> = if (secondNanoRepresentation) {
    composedEncoder {
        encode(it.epochSecond, longEncoder)
        encode(it.nano.toLong(), longEncoder)
    }
} else {
    longEncoder.withMappedInput(Instant::toEpochMilli)
}

@JvmOverloads
fun LocalTimeEncoder(decodeNanos: Boolean = false, intEncoder: Encoder<Int> = IntEncoder()): Encoder<LocalTime> =
    composedEncoder {
        encode(it.hour, intEncoder)
        encode(it.minute, intEncoder)
        encode(it.second, intEncoder)
        if (decodeNanos) {
            encode(it.nano, intEncoder)
        }
    }

fun LocalDateEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<LocalDate> =
    composedEncoder {
        encode(it.year, intEncoder)
        encode(it.monthValue, intEncoder)
        encode(it.dayOfMonth, intEncoder)
    }

fun DateEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<Date> = longEncoder.withMappedInput(Date::getTime)

fun ZoneIdEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<ZoneId> =
    stringEncoder.withMappedInput(ZoneId::getId)

fun ZonedDateTimeEncoder(
    zoneIdEncoder: Encoder<ZoneId> = ZoneIdEncoder(),
    instantEncoder: Encoder<Instant> = InstantEncoder(),
): Encoder<ZonedDateTime> =
    composedEncoder {
        encode(it.toInstant(), instantEncoder)
        encode(it.zone, zoneIdEncoder)
    }
