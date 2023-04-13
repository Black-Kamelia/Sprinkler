@file:JvmName("Encoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.encoder

import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Creates an [Encoder] that writes an [UUID] to the output. The [UUID] is encoded as two [Long]s, representing
 * [getMostSignificantBits()][UUID.getMostSignificantBits] and [getLeastSignificantBits()][UUID.getLeastSignificantBits]
 * methods.
 *
 * @param longEncoder the [Encoder] to use for writing the [Long]s (defaults to the default [LongEncoder])
 * @return the [Encoder] that writes the [UUID]
 */
@JvmOverloads
fun UUIDEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<UUID> =
    Encoder { obj, output ->
        longEncoder.encode(obj.mostSignificantBits, output)
        longEncoder.encode(obj.leastSignificantBits, output)
    }

/**
 * Creates an [Encoder] that writes an [UUID] to the output. The [UUID] is encoded as a [String], representing
 * [toString()][UUID.toString] method.
 *
 * @param stringEncoder the [Encoder] to use for writing the [String] (defaults to the default [UTF8StringEncoder])
 * @return the [Encoder] that writes the [UUID]
 */
@JvmOverloads
fun UUIDStringEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<UUID> =
    stringEncoder.withMappedInput(UUID::toString)

/**
 * Creates an [Encoder] that writes a [Pair] to the output. The [Pair] is represented as two values of type [T] and [U].
 * These values are written using the provided [firstEncoder] and [secondEncoder].
 *
 * @param firstEncoder the [Encoder] to use for writing the first value
 * @param secondEncoder the [Encoder] to use for writing the second value
 * @return the [Encoder] that writes the [Pair]
 * @param T the type of the first value
 * @param U the type of the second value
 */
fun <T, U> PairEncoder(firstEncoder: Encoder<T>, secondEncoder: Encoder<U>): Encoder<Pair<T, U>> =
    Encoder { obj, output ->
        firstEncoder.encode(obj.first, output)
        secondEncoder.encode(obj.second, output)
    }

/**
 * Creates an [Encoder] that writes an [Instant] to the output.
 *
 * &nbsp;
 *
 * The [Instant] can be encoded as either a single [Long] or as two [Long]s. For the single [Long] encoding,
 * the [Instant] is written using the [toEpochMilli()][Instant.toEpochMilli] method. For the two [Long] representation,
 * the [Instant] is written using the [getEpochSecond()][Instant.getEpochSecond] and [getNano()][Instant.getNano]
 * methods.
 *
 * &nbsp;
 *
 * **NOTE**: For the two [Long] encoding, the value of [getNano()][Instant.getNano] is converted from an int to a
 * [Long] before being written.
 *
 * &nbsp;
 *
 * @param secondNanoEncoding whether to use the two [Long] encoding (defaults to `false`)
 * @param longEncoder the [Encoder] to use for writing the [Long]s (defaults to the default [LongEncoder])
 * @return the [Encoder] that writes the [Instant]
 */
@JvmOverloads
fun InstantEncoder(
    secondNanoEncoding: Boolean = false,
    longEncoder: Encoder<Long> = LongEncoder(),
): Encoder<Instant> = if (secondNanoEncoding) {
    Encoder { obj, output ->
        longEncoder.encode(obj.epochSecond, output)
        longEncoder.encode(obj.nano.toLong(), output)
    }
} else {
    longEncoder.withMappedInput(Instant::toEpochMilli)
}

/**
 * Creates an [Encoder] that writes a [LocalTime] to the output. The [LocalTime] can be encoded as either a three [Int]
 * or four [Int]s.
 *
 * &nbsp;
 *
 * The [LocalTime] can be encoded as either a three [Int]s:
 *
 * - the hour with [getHour()][LocalTime.getHour]
 * - the minute with [getMinute()][LocalTime.getMinute]
 * - the second with [getSecond()][LocalTime.getSecond]
 *
 * as four [Int]s, the [LocalTime] is encoded using the three [Int]s above and the nanosecond with
 * [getNano()][LocalTime.getNano].
 *
 * @param encodeNanos whether to use the four [Int] representation (defaults to `false`)
 * @param intEncoder the [Encoder] to use for writing the [Int]s (defaults to the default [IntEncoder])
 * @return the [Encoder] that writes the [LocalTime]
 */
@JvmOverloads
fun LocalTimeEncoder(encodeNanos: Boolean = false, intEncoder: Encoder<Int> = IntEncoder()): Encoder<LocalTime> =
    Encoder { obj, output ->
        intEncoder.encode(obj.hour, output)
        intEncoder.encode(obj.minute, output)
        intEncoder.encode(obj.second, output)
        if (encodeNanos) {
            intEncoder.encode(obj.nano, output)
        }
    }

/**
 * Creates an [Encoder] that writes a [LocalDate] to the output.
 *
 * &nbsp;
 *
 * The [LocalDate] is encoded as three [Int]s:
 *
 * - the year with [getYear()][LocalDate.getYear]
 * - the month with [getMonthValue()][LocalDate.getMonthValue]
 * - the day of the month with [getDayOfMonth()][LocalDate.getDayOfMonth]
 *
 * @param intEncoder the [Encoder] to use for writing the [Int]s (defaults to the default [IntEncoder])
 * @return the [Encoder] that writes the [LocalDate]
 */
@JvmOverloads
fun LocalDateEncoder(intEncoder: Encoder<Int> = IntEncoder()): Encoder<LocalDate> =
    Encoder { obj, output ->
        intEncoder.encode(obj.year, output)
        intEncoder.encode(obj.monthValue, output)
        intEncoder.encode(obj.dayOfMonth, output)
    }

/**
 * Creates an [Encoder] that writes a [LocalDateTime] to the output. The [LocalDateTime] is encoded as a [LocalDate]
 * and a [LocalTime]. These values are written using the provided [localDateEncoder] and [localTimeEncoder].
 *
 * @param localDateEncoder the [Encoder] to use for writing the [LocalDate] (defaults to the default [LocalDateEncoder])
 * @param localTimeEncoder the [Encoder] to use for writing the [LocalTime] (defaults to the default [LocalTimeEncoder])
 * @return the [Encoder] that writes the [LocalDateTime]
 */
@JvmOverloads
fun LocalDateTimeEncoder(
    localDateEncoder: Encoder<LocalDate> = LocalDateEncoder(),
    localTimeEncoder: Encoder<LocalTime> = LocalTimeEncoder(),
): Encoder<LocalDateTime> =
    Encoder { obj, output ->
        localDateEncoder.encode(obj.toLocalDate(), output)
        localTimeEncoder.encode(obj.toLocalTime(), output)
    }

/**
 * Creates an [Encoder] that writes a [Date] to the output. The [Date] is encoded as a single [Long] using
 * [getTime()][Date.getTime] method.
 *
 * @param longEncoder the [Encoder] to use for writing the [Long] (defaults to the default [LongEncoder])
 * @return the [Encoder] that writes the [Date]
 */
@JvmOverloads
fun DateEncoder(longEncoder: Encoder<Long> = LongEncoder()): Encoder<Date> = longEncoder.withMappedInput(Date::getTime)

/**
 * Creates an [Encoder] that writes a [ZoneId] to the output. The [ZoneId] is encoded as a [String] using the
 * [getId()][ZoneId.getId] method.
 *
 * @param stringEncoder the [Encoder] to use for writing the [String] (defaults to the default [UTF8StringEncoder])
 * @return the [Encoder] that writes the [ZoneId]
 */
@JvmOverloads
fun ZoneIdEncoder(stringEncoder: Encoder<String> = UTF8StringEncoder()): Encoder<ZoneId> =
    stringEncoder.withMappedInput(ZoneId::getId)

/**
 * Creates an [Encoder] that writes a [ZonedDateTime] to the output. The [ZonedDateTime] is encoded as an [Instant] and
 * a [ZoneId]. These values are written using the provided [instantEncoder] and [zoneIdEncoder].
 *
 * @param instantEncoder the [Encoder] to use for writing the [Instant] (defaults to the default [InstantEncoder])
 * @param zoneIdEncoder the [Encoder] to use for writing the [ZoneId] (defaults to the default [ZoneIdEncoder])
 * @return the [Encoder] that writes the [ZonedDateTime]
 */
@JvmOverloads
fun ZonedDateTimeEncoder(
    instantEncoder: Encoder<Instant> = InstantEncoder(),
    zoneIdEncoder: Encoder<ZoneId> = ZoneIdEncoder(),
): Encoder<ZonedDateTime> =
    Encoder { obj, output ->
        instantEncoder.encode(obj.toInstant(), output)
        zoneIdEncoder.encode(obj.zone, output)
    }
