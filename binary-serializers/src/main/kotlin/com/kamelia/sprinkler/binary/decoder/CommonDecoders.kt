@file:JvmName("Decoders")
@file:JvmMultifileClass

package com.kamelia.sprinkler.binary.decoder

import com.kamelia.sprinkler.binary.decoder.composer.composedDecoder
import com.kamelia.sprinkler.binary.decoder.core.Decoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@JvmOverloads
fun UUIDDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<UUID> {
    var msb = 0L
    return longDecoder
        .mapTo { msb = it; longDecoder }
        .mapResult { UUID(msb, it) }
}

@JvmOverloads
fun UUIDDecoderString(stringDecoder: Decoder<String> = UTF8StringDecoderEM()): Decoder<UUID> =
    stringDecoder.mapResult(UUID::fromString)

fun <T, U> PairDecoder(firstDecoder: Decoder<T>, secondDecoder: Decoder<U>): Decoder<Pair<T, U>> {
    var f: T? = null
    return firstDecoder
        .mapTo { f = it; secondDecoder }
        .mapResult { (@Suppress("UNCHECKED_CAST") (f as T)) to it }
}

@JvmOverloads
fun InstantDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<Instant> {
    var seconds = 0L
    return longDecoder
        .mapTo { seconds = it; longDecoder }
        .mapResult { Instant.ofEpochSecond(seconds, it) }
}

@JvmOverloads
fun LocalTimeDecoder(intDecoder: Decoder<Int> = IntDecoder()): Decoder<LocalTime> {
    var hour = 0
    var minute = 0
    return intDecoder
        .mapTo { hour = it; intDecoder }
        .mapTo { minute = it; intDecoder }
        .mapResult { LocalTime.of(hour, minute, it) }
}

@JvmOverloads
fun LocalDateDecoder(intDecoder: Decoder<Int> = IntDecoder()): Decoder<LocalDate> {
    var year = 0
    var month = 0
    return intDecoder
        .mapTo { year = it; intDecoder }
        .mapTo { month = it; intDecoder }
        .mapResult { LocalDate.of(year, month, it) }
}

@JvmOverloads
fun LocalDateTimeDecoder(intDecoder: Decoder<Int> = IntDecoder()): Decoder<LocalDateTime> = composedDecoder {
    beginWith(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .then(intDecoder)
        .reduce(LocalDateTime::of)
}

@JvmOverloads
fun DateDecoder(longDecoder: Decoder<Long> = LongDecoder()): Decoder<Date> = longDecoder.mapResult(::Date)

@JvmOverloads
fun ZoneIdDecoder(stringDecoder: Decoder<String> = UTF8StringDecoderEM()): Decoder<ZoneId> =
    stringDecoder.mapResult(ZoneId::of)

@JvmOverloads
fun ZonedDateTimeDecoder(
    instantDecoder: Decoder<Instant> = InstantDecoder(),
    zoneIdDecoder: Decoder<ZoneId> = ZoneIdDecoder(),
): Decoder<ZonedDateTime> = composedDecoder {
    beginWith(instantDecoder)
        .then(zoneIdDecoder)
        .reduce(ZonedDateTime::ofInstant)
}
