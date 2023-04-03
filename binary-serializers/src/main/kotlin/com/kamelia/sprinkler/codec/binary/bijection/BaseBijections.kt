package com.kamelia.sprinkler.codec.binary.bijection

import com.kamelia.sprinkler.codec.binary.decoder.BooleanDecoder
import com.kamelia.sprinkler.codec.binary.decoder.ByteDecoder
import com.kamelia.sprinkler.codec.binary.decoder.DoubleDecoder
import com.kamelia.sprinkler.codec.binary.decoder.FloatDecoder
import com.kamelia.sprinkler.codec.binary.decoder.IntDecoder
import com.kamelia.sprinkler.codec.binary.decoder.LongDecoder
import com.kamelia.sprinkler.codec.binary.decoder.ShortDecoder
import com.kamelia.sprinkler.codec.binary.encoder.BooleanEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ByteEncoder
import com.kamelia.sprinkler.codec.binary.encoder.DoubleEncoder
import com.kamelia.sprinkler.codec.binary.encoder.FloatEncoder
import com.kamelia.sprinkler.codec.binary.encoder.IntEncoder
import com.kamelia.sprinkler.codec.binary.encoder.LongEncoder
import com.kamelia.sprinkler.codec.binary.encoder.ShortEncoder
import java.nio.ByteOrder

fun ByteBijection(): Bijection<Byte> = Bijection.of(ByteEncoder(), ::ByteDecoder)

@JvmOverloads
fun ShortBijection(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Bijection<Short> =
    Bijection.of(ShortEncoder(endianness)) { ShortDecoder(endianness) }

@JvmOverloads
fun IntBijection(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Bijection<Int> =
    Bijection.of(IntEncoder(endianness)) { IntDecoder(endianness) }

@JvmOverloads
fun LongBijection(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Bijection<Long> =
    Bijection.of(LongEncoder(endianness)) { LongDecoder(endianness) }

@JvmOverloads
fun FloatBijection(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Bijection<Float> =
    Bijection.of(FloatEncoder(endianness)) { FloatDecoder(endianness) }

@JvmOverloads
fun DoubleBijection(endianness: ByteOrder = ByteOrder.BIG_ENDIAN): Bijection<Double> =
    Bijection.of(DoubleEncoder(endianness)) { DoubleDecoder(endianness) }

fun BooleanBijection(): Bijection<Boolean> = Bijection.of(BooleanEncoder(), ::BooleanDecoder)


