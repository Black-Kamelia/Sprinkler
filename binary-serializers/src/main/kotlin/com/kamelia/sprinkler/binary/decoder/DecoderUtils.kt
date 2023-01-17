package com.kamelia.sprinkler.binary.decoder

fun <T> Decoder<T>.toOptional(): Decoder<T?> = object : Decoder<T?> {
    private var isPresent: Boolean? = null

    override fun decode(input: DecoderDataInput): Decoder.State<T?> {
        if (isPresent == null) {
            val byte = input.read()
            if (byte == -1) {
                return Decoder.State.Processing("Waiting nullability byte")
            }
            isPresent = byte == 1
        }

        return if (isPresent == true) {
            this@toOptional.decode(input)
        } else {
            Decoder.State.Done(null)
        }
    }

    override fun reset() = this@toOptional.reset()
}

