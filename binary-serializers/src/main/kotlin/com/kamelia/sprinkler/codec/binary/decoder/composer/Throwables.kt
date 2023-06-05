package com.kamelia.sprinkler.codec.binary.decoder.composer

import com.zwendo.restrikt.annotation.PackagePrivate

@PackagePrivate
internal object ProcessingMarker : Throwable() {

    override fun fillInStackTrace(): Throwable = this

}

@PackagePrivate
internal object RecursionMarker : Throwable() {

    override fun fillInStackTrace(): Throwable = this

}
