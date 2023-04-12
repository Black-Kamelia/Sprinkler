@file:JvmName("Decoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.codec.binary.decoder

import com.kamelia.sprinkler.codec.binary.decoder.composer.composedDecoder
import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Creates a [Decoder] that reads an [UUID] from the input. The [UUID] os represented as two [Long]s, and the final
 * [UUID] is created using the [UUID] constructor.
 *
 * @param longDecoder the [Decoder] to use for reading the [Long]s (defaults to the default [LongDecoder])
 * @return the [Decoder] that reads the [UUID]
 */
@JvmOverloads
fun UUIDDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<UUID> {
    var msb = 0L
    return longDecoder
        .mapTo { msb = it; longDecoder }
        .mapResult { UUID(msb, it) }
}

/**
 * Creates a [Decoder] that reads an [UUID] from the input. The [UUID] os represented as a [String].
 *
 * @param stringDecoder the [Decoder] to use for reading the [String] (defaults to the default [UTF8StringDecoder])
 * @return the [Decoder] that reads the [UUID]
 */
@JvmOverloads
fun UUIDDecoderString(stringDecoder: Decoder<String> = UTF8StringDecoder()): Decoder<UUID> =
    stringDecoder.mapResult(UUID::fromString)

/**
 * Creates a [Decoder] that reads a [Pair] from the input. The [Pair] is represented as two values of type [T] and [U].
 * These values are read using the provided [firstDecoder] and [secondDecoder].
 *
 * @param firstDecoder the [Decoder] to use for reading the first value
 * @param secondDecoder the [Decoder] to use for reading the second value
 * @return the [Decoder] that reads the [Pair]
 * @param T the type of the first value
 * @param U the type of the second value
 */
fun <T, U> PairDecoder(firstDecoder: Decoder<T>, secondDecoder: Decoder<U>): Decoder<Pair<T, U>> {
    var f: T? = null
    return firstDecoder
        .mapTo { f = it; secondDecoder }
        .mapResult { (@Suppress("UNCHECKED_CAST") (f as T)) to it }
}

/**
 * Creates a [Decoder] that reads an [Instant] from the input.
 *
 * Depending on the [secondNanoRepresentation] parameter, the [Instant] can be represented as a single [Long]:
 *
 * - the number of milliseconds since the epoch
 *
 * or as two [Long]s:
 *
 * - the number of seconds since the epoch
 * - the number of nanoseconds since the start of the second
 *
 * **NOTE**: As the [Instant.ofEpochMilli] or [Instant.ofEpochSecond] methods are used internally, the [Decoder.decode]
 * of the returned [Decoder] can throw an [ArithmeticException] or a [DateTimeException][java.time.DateTimeException] as
 * specified by the methods.
 *
 * &nbsp;
 *
 * @param secondNanoRepresentation `true` to represent the [Instant] as two [Long]s, false to represent the [Instant]
 * as a single [Long] (defaults to `false`)
 * @param longDecoder the [Decoder] to use for reading the [Long] (defaults to default [LongDecoder])
 * @return the [Decoder] that reads the [Instant]
 */
@JvmOverloads
fun InstantDecoder(
    secondNanoRepresentation: Boolean = false,
    longDecoder: Decoder<Long> = LongDecoder(),
): Decoder<Instant> {
    var seconds = 0L
    return longDecoder
        .run {
            if (secondNanoRepresentation) {
                mapTo { seconds = it; longDecoder }
            } else {
                this
            }
        }
        .mapResult {
            if (secondNanoRepresentation) {
                Instant.ofEpochSecond(seconds, it)
            } else {
                Instant.ofEpochMilli(it)
            }
        }
}

/**
 * Creates a [Decoder] that reads a [LocalTime] from the input.
 *
 * Depending on the [decodeNanos] parameter, the [LocalTime] can be represented as three [Int]s:
 *
 * - the hour
 * - the minute
 * - the second
 *
 * or as four [Int]s, using the same representation as [LocalTime.of] but with an additional [Int] for the nanoseconds.
 *
 * &nbsp;
 *
 * **NOTE**: As the [LocalTime.of] method overloads are used internally, the [Decoder.decode] method of the returned
 * [Decoder] can throw an [ArithmeticException] or a [DateTimeException][java.time.DateTimeException] as specified by
 * the method.
 *
 * &nbsp;
 *
 * @param decodeNanos `true` to represent the [LocalTime] as four [Int]s, false to represent the [LocalTime] as three
 * [Int]s (defaults to `false`)
 * @param intDecoder the [Decoder] to use for reading the [Int]s (defaults to the default [IntDecoder])
 * @return the [Decoder] that reads the [LocalTime]
 */
@JvmOverloads
fun LocalTimeDecoder(decodeNanos: Boolean = false, intDecoder: Decoder<Int> = IntDecoder()): Decoder<LocalTime> {
    var hour = 0
    var minute = 0
    var seconds = 0
    return intDecoder
        .mapTo { hour = it; intDecoder }
        .mapTo { minute = it; intDecoder }
        .run {
            if (decodeNanos) {
                mapTo { seconds = it; intDecoder }
            } else {
                this
            }
        }
        .mapResult {
            if (decodeNanos) {
                LocalTime.of(hour, minute, seconds, it)
            } else {
                LocalTime.of(hour, minute, it)
            }
        }
}

/**
 * Creates a [Decoder] that reads a [LocalDate] from the input. The [LocalDate] is represented as three [Int]s. The
 * final [LocalDate] is created using the [LocalDate.of] method.
 *
 * The [Int]s represent:
 *
 * - the year
 * - the month
 * - the day
 *
 * **NOTE**: As the [LocalDate.of] method is used internally, the [Decoder.decode] method of the returned [Decoder] can
 * throw a [DateTimeException][java.time.DateTimeException] as specified by the method.
 *
 * &nbsp;
 *
 * @param intDecoder the [Decoder] to use for reading the [Int]s (defaults to the default [IntDecoder])
 * @return the [Decoder] that reads the [LocalDate]
 */
@JvmOverloads
fun LocalDateDecoder(intDecoder: Decoder<Int> = IntDecoder()): Decoder<LocalDate> {
    var year = 0
    var month = 0
    return intDecoder
        .mapTo { year = it; intDecoder }
        .mapTo { month = it; intDecoder }
        .mapResult { LocalDate.of(year, month, it) }
}

/**
 * Creates a [Decoder] that reads a [LocalDateTime] from the input.
 *
 * Depending on the [decodeNanos] parameter, the [LocalDateTime] can be represented as six [Int]s:
 *
 * - the year
 * - the month
 * - the day
 * - the hour
 * - the minute
 * - the second
 *
 * or as seven [Int]s, using the same representation but with an additional [Int] for the nanosecond.
 *
 * &nbsp;
 *
 * **NOTE**: As the [LocalDateTime.of] method overloads are used internally, the [Decoder.decode] method of the returned
 * [Decoder] can throw a [DateTimeException][java.time.DateTimeException] as specified by the method.
 *
 * &nbsp;
 *
 * @param decodeNanos `true` to represent the [LocalDateTime] as seven [Int]s, false to represent the [LocalDateTime] as
 * six [Int]s (defaults to `false`)
 * @param intDecoder the [Decoder] to use for reading the [Int]s (defaults to the default [IntDecoder])
 * @return the [Decoder] that reads the [LocalDateTime]
 */
@JvmOverloads
fun LocalDateTimeDecoder(
    decodeNanos: Boolean = false,
    intDecoder: Decoder<Int> = IntDecoder(),
): Decoder<LocalDateTime> = composedDecoder {
    val decoder = beginWith(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)

    if (decodeNanos) {
        decoder.then(intDecoder).reduce(LocalDateTime::of)
    } else {
        decoder.reduce(LocalDateTime::of)
    }
}

/**
 * Creates a [Decoder] that reads a [Date] from the input.
 *
 * The [Date] is represented as a [Long]. The final [Date] is created using the [Date] constructor.
 *
 * @param longDecoder the [Decoder] to use for reading the [Long] (defaults to the default [LongDecoder])
 * @return the [Decoder] that reads the [Date]
 */
@JvmOverloads
fun DateDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<Date> = longDecoder.mapResult(::Date)

/**
 * Creates a [Decoder] that reads a [ZoneId] from the input. The [ZoneId] is represented as a [String]. The final
 * [ZoneId] is created using the [ZoneId.of] method.
 *
 * @param stringDecoder the [Decoder] to use for reading the [String] (defaults to the default [UTF8StringDecoder])
 * @return the [Decoder] that reads the [ZoneId]
 */
@JvmOverloads
fun ZoneIdDecoder(stringDecoder: Decoder<String> = UTF8StringDecoder()): Decoder<ZoneId> =
    stringDecoder.mapResult(ZoneId::of)

/**
 * Creates a [Decoder] that reads a [ZonedDateTime] from the input. The [ZonedDateTime] is represented as an [Instant]
 * and a [ZoneId]. The final [ZonedDateTime] is created using the [ZonedDateTime.ofInstant] method.
 *
 * @param instantDecoder the [Decoder] to use for reading the [Instant] (defaults to the default [InstantDecoder])
 * @param zoneIdDecoder the [Decoder] to use for reading the [ZoneId] (defaults to the default [ZoneIdDecoder])
 * @return the [Decoder] that reads the [ZonedDateTime]
 */
@JvmOverloads
fun ZonedDateTimeDecoder(
    instantDecoder: Decoder<Instant> = InstantDecoder(),
    zoneIdDecoder: Decoder<ZoneId> = ZoneIdDecoder(),
): Decoder<ZonedDateTime> = composedDecoder {
    beginWith(instantDecoder)
        .then(zoneIdDecoder)
        .reduce(ZonedDateTime::ofInstant)
}
