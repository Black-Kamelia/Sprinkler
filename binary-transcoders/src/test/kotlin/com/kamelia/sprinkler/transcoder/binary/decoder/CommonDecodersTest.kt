package com.kamelia.sprinkler.transcoder.binary.decoder

import com.kamelia.sprinkler.transcoder.binary.decoder.util.assertDoneAndGet
import com.kamelia.sprinkler.util.byte
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommonDecodersTest {

    @Test
    fun `uuid decoder works correctly`() {
        val decoder = UUIDDecoder()
        val uuid = UUID.randomUUID()

        val msb = uuid.mostSignificantBits
        val lsb = uuid.leastSignificantBits
        val data = byteArrayOf(
            msb.byte(7), msb.byte(6), msb.byte(5), msb.byte(4),
            msb.byte(3), msb.byte(2), msb.byte(1), msb.byte(0),
            lsb.byte(7), lsb.byte(6), lsb.byte(5), lsb.byte(4),
            lsb.byte(3), lsb.byte(2), lsb.byte(1), lsb.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(uuid, result)
    }

    @Test
    fun `uuid decoder string works correctly`() {
        val decoder = UUIDDecoderString()
        val uuid = UUID.randomUUID()

        val array = uuid.toString().toByteArray()
        val size = array.size
        val data = byteArrayOf(size.byte(3), size.byte(2), size.byte(1), size.byte(0)) + array

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(uuid, result)
    }

    @Test
    fun `pair decoder works correctly`() {
        val decoder = PairDecoder(IntDecoder(), IntDecoder())
        val data = byteArrayOf(0, 0, 0, 1, 0, 0, 0, 2)

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(1 to 2, result)
    }

    @Test
    fun `instant decoder works correctly`() {
        val decoder = InstantDecoder()
        val instant = Instant.now()

        val millis = instant.toEpochMilli()
        val data = byteArrayOf(
            millis.byte(7), millis.byte(6), millis.byte(5), millis.byte(4),
            millis.byte(3), millis.byte(2), millis.byte(1), millis.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(instant.toEpochMilli(), result.toEpochMilli())
    }

    @Test
    fun `instant decoder works correctly with nanos`() {
        val decoder = InstantDecoder(true)
        val instant = Instant.now()

        val millis = instant.epochSecond
        val nanos = instant.nano.toLong()
        val data = byteArrayOf(
            millis.byte(7), millis.byte(6), millis.byte(5), millis.byte(4),
            millis.byte(3), millis.byte(2), millis.byte(1), millis.byte(0),
            nanos.byte(7), nanos.byte(6), nanos.byte(5), nanos.byte(4),
            nanos.byte(3), nanos.byte(2), nanos.byte(1), nanos.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(instant.toEpochMilli(), result.toEpochMilli())
        assertEquals(instant.nano, result.nano)
    }

    @Test
    fun `local time decoder works correctly`() {
        val decoder = LocalTimeDecoder(true)
        val localTime = LocalTime.now()

        val hours = localTime.hour
        val minutes = localTime.minute
        val seconds = localTime.second
        val nanos = localTime.nano
        val data = byteArrayOf(
            hours.byte(3), hours.byte(2), hours.byte(1), hours.byte(0),
            minutes.byte(3), minutes.byte(2), minutes.byte(1), minutes.byte(0),
            seconds.byte(3), seconds.byte(2), seconds.byte(1), seconds.byte(0),
            nanos.byte(3), nanos.byte(2), nanos.byte(1), nanos.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(localTime, result)
    }

    @Test
    fun `local time decoder without nanos works correctly`() {
        val decoder = LocalTimeDecoder(false)
        val localTime = LocalTime.now().withNano(0)

        val hours = localTime.hour
        val minutes = localTime.minute
        val seconds = localTime.second
        val data = byteArrayOf(
            hours.byte(3), hours.byte(2), hours.byte(1), hours.byte(0),
            minutes.byte(3), minutes.byte(2), minutes.byte(1), minutes.byte(0),
            seconds.byte(3), seconds.byte(2), seconds.byte(1), seconds.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(localTime, result)
    }

    @Test
    fun `local date decoder works correctly`() {
        val decoder = LocalDateDecoder()
        val localDate = LocalDate.now()

        val year = localDate.year
        val month = localDate.monthValue
        val day = localDate.dayOfMonth
        val data = byteArrayOf(
            year.byte(3), year.byte(2), year.byte(1), year.byte(0),
            month.byte(3), month.byte(2), month.byte(1), month.byte(0),
            day.byte(3), day.byte(2), day.byte(1), day.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(localDate, result)
    }

    @Test
    fun `local date time decoder works correctly`() {
        val decoder = LocalDateTimeDecoder(localTimeDecoder = LocalTimeDecoder(true))
        val localDateTime = LocalDateTime.now()

        val year = localDateTime.year
        val month = localDateTime.monthValue
        val day = localDateTime.dayOfMonth
        val hours = localDateTime.hour
        val minutes = localDateTime.minute
        val seconds = localDateTime.second
        val nanos = localDateTime.nano
        val data = byteArrayOf(
            year.byte(3), year.byte(2), year.byte(1), year.byte(0),
            month.byte(3), month.byte(2), month.byte(1), month.byte(0),
            day.byte(3), day.byte(2), day.byte(1), day.byte(0),
            hours.byte(3), hours.byte(2), hours.byte(1), hours.byte(0),
            minutes.byte(3), minutes.byte(2), minutes.byte(1), minutes.byte(0),
            seconds.byte(3), seconds.byte(2), seconds.byte(1), seconds.byte(0),
            nanos.byte(3), nanos.byte(2), nanos.byte(1), nanos.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(localDateTime, result)
    }

    @Test
    fun `local date time decoder without nanos works correctly`() {
        val decoder = LocalDateTimeDecoder()
        val localDateTime = LocalDateTime.now().withNano(0)

        val year = localDateTime.year
        val month = localDateTime.monthValue
        val day = localDateTime.dayOfMonth
        val hours = localDateTime.hour
        val minutes = localDateTime.minute
        val seconds = localDateTime.second
        val data = byteArrayOf(
            year.byte(3), year.byte(2), year.byte(1), year.byte(0),
            month.byte(3), month.byte(2), month.byte(1), month.byte(0),
            day.byte(3), day.byte(2), day.byte(1), day.byte(0),
            hours.byte(3), hours.byte(2), hours.byte(1), hours.byte(0),
            minutes.byte(3), minutes.byte(2), minutes.byte(1), minutes.byte(0),
            seconds.byte(3), seconds.byte(2), seconds.byte(1), seconds.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(localDateTime, result)
    }

    @Test
    fun `date time decoder works correctly`() {
        val decoder = DateDecoder()
        val dateTime = Date()

        val time = dateTime.time
        val data = byteArrayOf(
            time.byte(7), time.byte(6), time.byte(5), time.byte(4),
            time.byte(3), time.byte(2), time.byte(1), time.byte(0)
        )

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(dateTime, result)
    }

    @Test
    fun `zoneId decoder works correctly`() {
        val decoder = ZoneIdDecoder()
        val zoneId = ZoneId.systemDefault()

        val array = zoneId.id.toByteArray()
        val size = array.size
        val data = byteArrayOf(
            size.byte(3), size.byte(2), size.byte(1), size.byte(0)
        ) + array

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(zoneId, result)
    }

    @Test
    fun `zoned date time decoder works correctly`() {
        val decoder = ZonedDateTimeDecoder()
        val zonedDateTime = ZonedDateTime.now().withNano(0)

        val instant = zonedDateTime.toInstant().toEpochMilli()
        val zoneId = zonedDateTime.zone

        val array = zoneId.id.toByteArray()
        val size = array.size
        val data = byteArrayOf(
            instant.byte(7), instant.byte(6), instant.byte(5), instant.byte(4),
            instant.byte(3), instant.byte(2), instant.byte(1), instant.byte(0),
            size.byte(3), size.byte(2), size.byte(1), size.byte(0)
        ) + array

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(zonedDateTime, result)
    }

    @Test
    fun `zoned date time decoder with nanos works correctly`() {
        val decoder = ZonedDateTimeDecoder(InstantDecoder(true))
        val zonedDateTime = ZonedDateTime.now()

        val instant = zonedDateTime.toInstant()
        val seconds = instant.epochSecond
        val nanos = instant.nano.toLong()
        val zoneId = zonedDateTime.zone

        val array = zoneId.id.toByteArray()
        val size = array.size
        val data = ByteArray(Long.SIZE_BYTES * 2 + Int.SIZE_BYTES + array.size) {
            when (it) {
                in 0..7 -> seconds.byte(it, false)
                in 8..15 -> nanos.byte(it - 8, false)
                in 16..19 -> size.byte(it - 16, false)
                else -> array[it - 20]
            }
        }

        val result = decoder.decode(data).assertDoneAndGet()
        assertEquals(zonedDateTime, result)
    }

}
