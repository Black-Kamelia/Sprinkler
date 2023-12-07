# Sprinkler: i18n (internationalization)

[![Maven Central](https://img.shields.io/maven-central/v/com.black-kamelia.sprinkler/i18n)](https://central.sonatype.com/artifact/com.black-kamelia.sprinkler/i18n)

## Summary

- [Intentions](#intentions)
- [TranslatorBuilder](#translatorbuilder)
  - [Representation](#representation)
  - [Adding translations](#adding-translations)
  - [DuplicatedKeyResolution](#duplicatedkeyresolution)
  - [Settings](#settings)
  - [Building](#building)
- [Changelog](#changelog)

## Intentions

This module provides a simple way to manage internationalization in a Java/Kotlin project, with features such as
variable interpolation and formatting, and automatic loading of translation files

## TranslatorBuilder

Before anyone can use the `Translator` interface, which will be covered in the next section, one has to first build
an instance of it.

Thankfully, `TranslatorBuilder` exists for this purpose. The goal is to provide it with different sources of translation
associations. 

### Representation

Indeed, a translation is a key-value pair, where the key is a `TranslationKey` (which is a type alias for
`String`), and the value is a `TranslationSourceData` (which is a type alias for `Any`) and corresponds to the
translation itself. This key-value pair is itself associated to a `Locale`.

In fact, a `TranslationKey` is a `String` that must follow a specific format, and here are some examples of valid keys:
Here are some examples of valid `TranslationKeys`:
- `my-root.my-node.my-key`
- `my_root.my_node.my_key`
- `myRoot.myNode.myKey`
- `my-root.my-node.my-table.1`

Any value that does not respect these rules will result in an exception being thrown. For more details on the format of
`TranslationKeys`, please refer to the documentation of the `TranslationKey` type alias.

A set of those associations is a `TranslationSourceMap`, which is a type alias for 
`Map<TranslationKey, TranslationSourceData>`.

### Adding translations

To start off, one has to obtain an instance of `TranslatorBuilder` thanks to the `Translator::builder` static method, 
which takes a default locale as a parameter. This locale will be used as the required locale for the translations.

```kt
val builder = Translator.builder(Locale.ENGLISH)
```

The primitive way to add translations is to use the `TranslatorBuilder::addMap` method, which associates a `Locale` to
a `TranslationSourceMap`. This method can be called multiple times, and the `TranslationSourceMap`s will be merged
(accordingly to the [`DuplicatedKeyResolution`](#duplicatedkeyresolution) strategy).

```kt
val builder = Translator.builder(Locale.ENGLISH)
    .addMap(Locale.ENGLISH, mapOf(
        "foo" to "My translation",
        "bar" to "My other translation"
    ))
    .addMap(Locale.FRENCH, mapOf(
        "foo" to "Ma traduction",
        "bar" to "Mon autre traduction"
    ))
```

(The `TranslatorBuilder::addMaps` works in very much the same way, except that it takes a 
`Map<Locale, TranslationSourceMap>` with all the associations at once.)

However, the method that is most likely to be used is `TranslatorBuilder::addFile`, which takes in a path as either a 
`File` or a `Path` and will load the translations from it. Said path must point to a file, or a directory containing
files that must respect the following rules:
- The file must be either a JSON file or a YAML file (the extension is used to determine which parser to use)
- The file name must correspond to the locale identifier that is desired, as it will be inferred automatically from
  it (for example, `en.json` will be inferred as `Locale.ENGLISH`)

Say we have the following directory structure:
```
assets/
├── locales/
│   ├── en.json
│   └── fr.json
└── ...
```

Then, we can load the translations from it like so:
```kt
val builder = Translator.builder(Locale.ENGLISH)
    .addFile(Path.of("assets/locales"))
```

The builder will automatically load the translations from the files, for the locales `Locale.ENGLISH` and
`Locale.FRENCH`.

### DuplicatedKeyResolution

When merging multiple `TranslationSourceMap`s, it is possible that some keys are duplicated. In this case, the
`DuplicatedKeyResolution` strategy is used to determine which value should be kept. One can set this strategy by using
the `TranslatorBuilder::withDuplicatedKeyResolutionPolicy` method. The possible values are:
- `DuplicatedKeyResolutionPolicy.FAIL`: throws an exception when a duplicated key is found. The exception will be thrown
  when the `TranslatorBuilder::build` method is called.
- `DuplicatedKeyResolutionPolicy.KEEP_FIRST`: keeps the first value that was added for a given key.
- `DuplicatedKeyResolutionPolicy.KEEP_LAST`: keeps the last value that was added for a given key.

### Settings

The `TranslatorBuilder` also has a few settings that can be set with a few other methods:
- `TranslatorBuilder::withDefaultLocale`: sets the default locale that will be used for the translations. This is the
  same as the parameter of the `Translator::builder` method.
- `TranslatorBuilder::withCurrentLocale`: sets the locale that will be in use when the `Translator::build` method is
  called.
- `TranslatorBuilder::withConfiguration`: sets the configuration of the `Translator`. 
  See the [Configuration](#configuration) section for more details.

### Building

Once all the translations have been added, and the configuration has be set as wanted, the `TranslatorBuilder::build` 
method can be called to obtain an instance of `Translator`.

```kt
val translator = Translator.builder(Locale.ENGLISH)
    .addFile(Path.of("assets/locales"))
    ...
    .build()
```

## Translator

`Translators` are immutable structure, and therefore thread-safe. None of their methods will ever modify their internal
state of the object.

This is important to understand, as the method to change the current locale, `Translator::withCurrentLocale`, returns
a new instance of `Translator` with the new locale.

### Basic usage

At its most basic level, one can use the `Translator::t` (as in "translate") and `Translator::tn`(as in "translate or 
null") methods to obtain the value associated to a `TranslationKey` for the current locale.

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("foo") // "My translation"
translator.t("bar") // "My other translation"

val translatorFr = translator.withCurrentLocale(Locale.FRENCH)
translatorFr.t("foo") // "Ma traduction"
translatorFr.t("bar") // "Mon autre traduction"
```

However, it is to be noted that one of the overloads of these methods can take a `Locale` as a parameter, which will
be used instead of the current locale.

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("foo", Locale.ENGLISH) // "My translation"
translator.t("foo", Locale.FRENCH) // "Ma traduction"
```

Note that if the key is not found for the given locale, it will first try to fall back to searching for the key in the
default locale, and if it is not found there either, the behavior will depend on the `MissingKeyPolicy` set in the
configuration of the `Translator` (see the [Configuration](#configuration) section for more details) in case of
the `Translator::t` method, and will simply return `null` in case of the `Translator::tn` method.

### Variable interpolation

Translations may contain variables, which can be interpolated with the `Translator::t` and `Translator::tn` methods.
A variable must be delimited by an opening and a closing pair of symbols, which can be set with the configuration of the
`Translator` (see the [Configuration](#configuration) section for more details). 
By default, the symbols are `{` and `}`.

Here is a few example of translations with variables:
```json
{
  "hello": "Hello, {name}!",
  "apple_dish": "With {apples} apple(s), you can make {dishes} dish(es).",
  "indexed_trio": "Hello, {0}, {1}, and {2}!",
  "unnamed_trio": "Hello, {}, {}, and {}!"
}
```

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("hello", mapOf("name" to "John")) // "Hello, John!"
translator.t("apple_dish", mapOf("apples" to 3, "dishes" to 2)) // "With 3 apple(s), you can make 2 dish(es)."
translator.t("indexed_trio", listOf("John", "Jane", "Jack")) // "Hello, John, Jane, and Jack!"
translator.t("unnamed_trio", listOf("John", "Jane", "Jack")) // "Hello, John, Jane, and Jack!"
```

In fact, the rules for simple variable interpolation are the exact same as proposed by the 
[Sprinkler Utils interpolation module](../utils/README.md#interpolation). Please refer to its documentation for more
details.

### Options

## Changelog

[Changelog](CHANGELOG.md)
