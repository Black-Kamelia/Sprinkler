# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Added

- String interpolation API. ([#45](https://github.com/Black-Kamelia/Sprinkler/issues/45))
- Added the `illegalArgument` and `assertionFailed` functions to throw Throwable with a message.
([!47](https://github.com/Black-Kamelia/Sprinkler/pull/47))

## 2.0.0 (2023-07-31)

### BREAKING CHANGES

#### Removed

- `KotlinDslAdapter` interface has been moved to the `jvm-bridge` module.
([#30](https://github.com/Black-Kamelia/Sprinkler/issues/30))
- `LambdaAdapters`, `InvokeExtensions` and `CollectorExtensions` classes have been moved to the `jvm-bridge` module.
([#40](https://github.com/Black-Kamelia/Sprinkler/issues/40))

### Added

- `castOrNull` extension to cast `Any?` to `T?` if possible.
- `setValue' operator for `Box.Mutable` to allow setting the value through the property syntax.

## 1.0.0 (2023-07-27)

Initial release.
