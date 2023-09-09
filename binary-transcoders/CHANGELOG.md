# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## SNAPSHOT

### BREAKING CHANGES

#### Changed

- The default value of the `stringEncoder` parameter of the `composedEncoder` function is now an `UTF8Encoder` with an
  endianness corresponding to the given `endianness` that is passed to the function, while it used to always be in big
  endian before. ([#55](https://github.com/Black-Kamelia/Sprinkler/issues/55))
- The default value of the `stringDecoder` parameter of the `composedDecoder` function is now an `UTF8Decoder` with an
  endianness corresponding to the given `endianness` that is passed to the function, while it used to always be in big
  endian before. ([#55](https://github.com/Black-Kamelia/Sprinkler/issues/55))

## 0.2.0 (2023-08-14)

### BREAKING CHANGES

#### Changed

- `EncodingScope` interface now extends `KotlinDslAdapter` interface from the `jvm-bridge` module instead of the`utils`
  module. ([#30](https://github.com/Black-Kamelia/Sprinkler/issues/30))
- `DecoderInput::read` and `DecoderInput::readBits` methods now return -1 when there is nothing to be read anymore, instead of 0, which is only 
  returned if the requested length to read is 0 itself (if the byte array's length is 0, for example).

## 0.1.0 (2023-07-27)

Initial release.
