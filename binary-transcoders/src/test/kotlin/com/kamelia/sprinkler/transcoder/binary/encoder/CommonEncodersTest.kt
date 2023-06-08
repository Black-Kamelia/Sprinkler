package com.kamelia.sprinkler.transcoder.binary.encoder

import com.kamelia.sprinkler.util.readInt
import com.kamelia.sprinkler.util.readLong
import com.kamelia.sprinkler.util.readString
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommonEncodersTest {

    @Test
    fun `uuid encoder works correctly`() {
        val encoder = UUIDEncoder()
        val uuid = UUID.randomUUID()

        val array = encoder.encode(uuid)

        val msb = array.readLong()
        val lsb = array.readLong(start = Long.SIZE_BYTES)

        assertEquals(uuid, UUID(msb, lsb))
    }

    @Test
    fun `uuid encoder string works correctly`() {
        val encoder = UUIDStringEncoder()
        val uuid = UUID.randomUUID()

        val array = encoder.encode(uuid)

        val size = array.readInt()
        val string = array.readString(start = Int.SIZE_BYTES, length = size)

        assertEquals(uuid, UUID.fromString(string))
    }

    @Test
    fun `pair encoder works correctly`() {
        val encoder = PairEncoder(IntEncoder(), IntEncoder())
        val pair = 1 to 2

        val array = encoder.encode(pair)

        val first = array.readInt()
        val second = array.readInt(start = Int.SIZE_BYTES)

        assertEquals(pair, first to second)
    }

    @Test
    fun `instant encoder works correctly`() {
        val encoder = InstantEncoder()
        val instant = Instant.now().let {
            it.minusNanos(it.nano.toLong())
        }

        val array = encoder.encode(instant)

        val result = array.readLong()

        assertEquals(instant, Instant.ofEpochMilli(result))
    }

    @Test
    fun `instant encoder works correctly with nanos`() {
        val encoder = InstantEncoder(true)
        val instant = Instant.now()

        val array = encoder.encode(instant)
        val seconds = array.readLong()
        val nanos = array.readLong(start = Long.SIZE_BYTES)

        assertEquals(instant, Instant.ofEpochSecond(seconds, nanos))
    }

    @Test
    fun `local time encoder works correctly`() {
        val encoder = LocalTimeEncoder()
        val localTime = LocalTime.now().let {
            it.minusNanos(it.nano.toLong())
        }

        val array = encoder.encode(localTime)
        val hour = array.readInt()
        val minute = array.readInt(start = Int.SIZE_BYTES)
        val second = array.readInt(start = Int.SIZE_BYTES * 2)

        assertEquals(localTime, LocalTime.of(hour, minute, second))
    }

    @Test
    fun `local date encoder works correctly`() {
        val encoder = LocalDateEncoder()
        val localDate = LocalDate.now()

        val array = encoder.encode(localDate)
        val year = array.readInt()
        val month = array.readInt(start = Int.SIZE_BYTES)
        val day = array.readInt(start = Int.SIZE_BYTES * 2)

        assertEquals(localDate, LocalDate.of(year, month, day))
    }

    @Test
    fun `local date time encoder works correctly`() {
        val encoder = LocalDateTimeEncoder()
        val localDateTime = LocalDateTime.now().let {
            it.minusNanos(it.nano.toLong())
        }

        val array = encoder.encode(localDateTime)
        val year = array.readInt()
        val month = array.readInt(start = Int.SIZE_BYTES)
        val day = array.readInt(start = Int.SIZE_BYTES * 2)
        val hour = array.readInt(start = Int.SIZE_BYTES * 3)
        val minute = array.readInt(start = Int.SIZE_BYTES * 4)
        val second = array.readInt(start = Int.SIZE_BYTES * 5)

        assertEquals(localDateTime, LocalDateTime.of(year, month, day, hour, minute, second))
    }

    @Test
    fun `local date time encoder works correctly with nanos`() {
        val encoder = LocalDateTimeEncoder(localTimeEncoder = LocalTimeEncoder(true))
        val localDateTime = LocalDateTime.now()

        val array = encoder.encode(localDateTime)
        val year = array.readInt()
        val month = array.readInt(start = Int.SIZE_BYTES)
        val day = array.readInt(start = Int.SIZE_BYTES * 2)
        val hour = array.readInt(start = Int.SIZE_BYTES * 3)
        val minute = array.readInt(start = Int.SIZE_BYTES * 4)
        val second = array.readInt(start = Int.SIZE_BYTES * 5)
        val nanos = array.readInt(start = Int.SIZE_BYTES * 6)

        assertEquals(localDateTime, LocalDateTime.of(year, month, day, hour, minute, second, nanos))
    }

    @Test
    fun `date encoder works correctly`() {
        val encoder = DateEncoder()
        val date = Date()

        val array = encoder.encode(date)
        val time = array.readLong()

        assertEquals(date, Date(time))
    }

    @Test
    fun `zone id encoder works correctly`() {
        val encoder = ZoneIdEncoder()
        val zoneId = TimeZone.getDefault().toZoneId()

        val array = encoder.encode(zoneId)
        val size = array.readInt()
        val string = array.readString(start = Int.SIZE_BYTES, length = size)

        assertEquals(zoneId, TimeZone.getTimeZone(string).toZoneId())
    }

    @Test
    fun `zoned date time encoder works correctly`() {
        val encoder = ZonedDateTimeEncoder()
        val zonedDateTime = ZonedDateTime.now().let {
            it.minusNanos(it.nano.toLong())
        }

        val array = encoder.encode(zonedDateTime)
        println(array.contentToString())
        val instant = array.readLong()
        val zoneIdSize = array.readInt(start = Long.SIZE_BYTES)
        val zoneIdString = array.readString(
            start = Long.SIZE_BYTES + Int.SIZE_BYTES,
            length = zoneIdSize
        )

        assertEquals(
            zonedDateTime,
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(instant),
                TimeZone.getTimeZone(zoneIdString).toZoneId()
            )
        )
    }

    @Test
    fun `zoned date time encoder works correctly with nanos`() {
        val encoder = ZonedDateTimeEncoder(InstantEncoder(true))
        val zonedDateTime = ZonedDateTime.now()

        val array = encoder.encode(zonedDateTime)
        val instantSeconds = array.readLong()
        val instantNanos = array.readLong(start = Long.SIZE_BYTES)
        val zoneIdSize = array.readInt(start = Long.SIZE_BYTES * 2)
        val zoneIdString = array.readString(
            start = Long.SIZE_BYTES * 2 + Int.SIZE_BYTES,
            length = zoneIdSize
        )

        assertEquals(
            zonedDateTime,
            ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(instantSeconds, instantNanos),
                TimeZone.getTimeZone(zoneIdString).toZoneId()
            )
        )
    }

}
