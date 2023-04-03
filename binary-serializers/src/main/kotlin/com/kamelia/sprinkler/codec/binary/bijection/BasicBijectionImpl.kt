package com.kamelia.sprinkler.codec.binary.bijection

import com.kamelia.sprinkler.codec.binary.decoder.core.Decoder
import com.kamelia.sprinkler.codec.binary.encoder.core.Encoder

class BasicBijectionImpl<T>(
    override val encoder: Encoder<T>,
    override val decoderFactory: () -> Decoder<T>
) : Bijection<T>
