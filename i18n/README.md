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
- [Translator](#translator)
  - [Basic usage](#basic-usage)
  - [Sections](#sections)
  - [`extraArgs`](#extraargs)
    - [Simple variables](#simple-variables)
    - [Options](#options)
    - [Formatting](#formatting)
- [Configuration](#configuration)
  - [`interpolationDelimiter`](#interpolationdelimiter)
  - [`pluralMapper`](#pluralmapper)
  - [`formats`](#formats)
  - [`missingKeyPolicy`](#missingkeypolicy)
- [Changelog](#changelog)

## Intentions

This module provides a simple way to manage internationalization in a Java/Kotlin project, with features such as
variable interpolation and formatting, and loading of translation files.

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

`TranslationSourceData` is in fact a type alias of `Any`, but is restricted at runtime and can be one of the following 
types:
- `String`
- `Boolean`
- Subtype of `Number` (e.g. `Int`, `Long`, `Double`, etc.)
- `List` of `TranslationSourceData`
- `TranslationSourceMap`

Whenever a function accepts a `TranslationSourceData`, it is the responsibility of the caller to ensure that the
given value respects the rules above. Any value that does not respect these rules will result in an exception being
thrown.

> **NOTE**: The exact syntax of `TranslationKeys` (and all syntactic elements) can be found in the `TypeAliases.kt`
> file. It also provides the regexes these elements must respect.

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
val translator = Translator.builder(Locale.ENGLISH)
    .addMap(Locale.ENGLISH, mapOf(
        "foo" to "My translation",
        "bar" to "My other translation"
    ))
    .addMap(Locale.FRENCH, mapOf(
        "foo" to "Ma traduction",
        "bar" to "Mon autre traduction"
    ))
    .build()
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
val translator = Translator.builder(Locale.ENGLISH)
    .addFile(Path.of("assets/locales"))
    .build()
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

Note that actual computing and merging of the translations will only be done when the `TranslatorBuilder::build` method
is called. This means that if there are duplicated keys, the `DuplicatedKeyResolutionPolicy` will be triggered at this
point, and that files will only be loaded at this point as well.

## Translator

This is important to understand, as the method to change the current locale, `Translator::withCurrentLocale`, returns
a new instance of `Translator` with the new locale.

### Basic usage

At its most basic level, one can use the `Translator::t` (as in "translate") and `Translator::tn`(as in "translate or 
null") methods to obtain the value associated to a `TranslationKey` for the given locale.

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("foo", Locale.ENGLISH) // "My translation"
translator.t("foo", Locale.FRENCH) // "Ma traduction"
```

Note that if the key is not found for the given locale, the resulting behavior will depend on a few things.
Indeed, there are several overloads of the `Translator::t` and `Translator::tn` methods, and some of them take in a few
more parameters, such as:
- a `fallbackLocale`, which is the locale that will be used if the key is not found for the given locale. If this
  parameter is not set, then the `Translator`'s default locale will be used.
- a vararg `fallbacks`, which are fallback keys that will be used if the key is not found for the given locale.

Returns the translation using the provided information. If the translation is not found, it will return null.

The order of resolution is the following:
- First, the translation is searched for the given `key` and `locale`.
- Then, it will try to find a valid translation for the keys provided in `fallbacks` in order.
- The next step is, if the `locale` is different from the `fallbackLocale`, to repeat the previous steps using
the `fallbackLocale` instead of the `locale`.
- Finally, if no translation is found, the behavior will depend on the `MissingKeyPolicy` set in the
configuration of the `Translator` (see the [Configuration](#configuration) section for more details) in case of
the `Translator::t` method, and will simply return `null` in case of the `Translator::tn` method.

It is to be noted that a `Translator` also has a `currentLocale` property. To change this property, one can use
the `Translator::withCurrentLocale` method, which returns a new instance of `Translator` with the new locale.

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("foo") // "My translation"

val translatorFr = translator.withCurrentLocale(Locale.FRENCH)
translatorFr.t("foo") // "Ma traduction"
```

### Sections

To avoid having to repeat the same prefix for all the keys, one can use the `Translator::section` method, which returns
a new instance of `Translator` with the given prefix. This prefix will be prepended to all the keys that are passed to
the `Translator::t` and `Translator::tn` methods.

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
val translatorFoo = translator.section("foo")
translatorFoo.t("bar") // // will look for the key "foo.bar"
```

### `extraArgs`

Calls to the `Translator::t` and `Translator::tn` methods can take in a optional `Map<String, Any>` as an argument,
which is called `extraArgs`. This map can contain extra arguments that will be used during the translation process.

#### Simple variables

Translations may contain variables, which can be interpolated with the `Translator::t` and `Translator::tn` methods.
A variable must be delimited by an opening and a closing pair of symbols, which can be set with the configuration of the
`Translator` (see the [Configuration](#configuration) section for more details). 
By default, the symbols are `{{` and `}}` (which is defined in the 
[Sprinkler Utils interpolation module](../utils/README.md#interpolation)).

Here is a few example of translations with variables:
```json
{
  "hello": "Hello, {{name}}!",
  "apple_dish": "With {{apples}} apple(s), you can make {{dishes}} dish(es)."
}
```

These variables can be interpolated with the `Translator::t` and `Translator::tn` methods, by passing the corresponding
values in the `extraArgs` map:

```kt
val translator = Translator.builder(Locale.ENGLISH)...build()
translator.t("hello", mapOf("name" to "John")) // "Hello, John!"
translator.t("apple_dish", mapOf("apples" to 3, "dishes" to 2)) // "With 3 apple(s), you can make 2 dish(es)."
```

In fact, the rules for simple variable interpolation are the exact same as proposed by the 
[Sprinkler Utils interpolation module](../utils/README.md#interpolation). Please refer to its documentation for more
details.

#### Options

There is also a special argument: `"options"`, which is a `Map` of option names (`String`) to option values (`Any`).
The way to pass these options is as follows:
```kt
translator.t("foo", mapOf("options" to mapOf("count" to 3)))
```

Here are the options that are currently supported:
- `context`: The value must be a `String`. The most common use case is to disambiguate gender, like in the following 
  example:
  ```kt
  // content:
  // {
  //   "greetings_male": "Hello mister",
  //   "greetings_female": "Hello miss"
  // }
  val translator: Translator = ...
  
  fun greetings(isMale: Boolean) {
      val value = translator.t(
          "greetings",
          mapOf("options" to mapOf("context" to (if (isMale) "male" else "female")))
      )
      println(value)
  }
  ```
  As shown in the example above, the context actually appends the value to the key, separated by an underscore.
  **NOTE**: The context is appended to the key before the **_plural_** value (e.g. `key_male_one`).
- `count`: The value must be a positive `Int`. The most common use case is to disambiguate the plural form of a word, 
  like in the following example:
  ```kt
  // content:
  // {
  //   "item_zero": "I have no items",
  //   "item_one": "I have one item",
  //   "item_other": "I have several items"
  // }
  val translator: Translator = ...
  
  fun items(count: Int) {
     val value = translator.t(
         "item",
         mapOf("options" to mapOf("count" to count))
     )
     println(value)
  }
  ```
  As shown in the example above, the plural value actually appends the value to the key, separated by an underscore.
  **NOTE**: The plural value is appended to the key after the **_context_** (e.g. `key_male_one`).
- `ordinal`: The value must be a `Boolean`. The most common use case is to disambiguate the ordinal form of a word, 
  like in the following example:
   ```kt
   // content:
   // {
   //   "item_ordinal_one": "I arrived {count}st",
   //   "item_ordinal_two": "I arrived {count}nd",
   //   "item_ordinal_few": "I arrived {count}rd",
   //   "item_ordinal_other": "I arrived {rank}th"
   // }
   val translator: Translator = ...
  
   fun rank(count: Int) {
      val value = translator.t(
          "item",
          mapOf("options" to mapOf(
              "count" to count,
              "ordinal" to true
          ))
      )
      println(value)
   }
   ```
   As shown in the example above, the `ordinal` literal is appended to the key, separated by an underscore.
   **NOTE**: The value is right before the **_plural_** value (e.g. a possible key with `key_male_ordinal_one`).

Note that there is a cleaner way to use these options thanks to the `options` factory, and the constants available in
the `Options` object:
```kt
// content:
// {
//   "contest_male_ordinal_one": "{name} made a pie with his {fruits}, and arrived {count}st",
//   ...
// }

val string = translator.t(
    "contest",
    mapOf(
        "name" to "James",
        "fruits" to "apples",
        options(Options.COUNT to 1, Options.ORDINAL to true, Options.CONTEXT to "male")
    )
)
println(string) // "James made a pie with his apples, and arrived 1st"
```

The resolution order for the variable names is the following:
- First, the name is searched for in the `extraArgs` map.
- If it is not found, then the name is searched for in the `options` map (which is why one can use the "count" variable
  by only passing it in the `options` map).

#### Formatting

When declaring a variable in a translation string, it is also possible to specify a format for it. This is done by
appending a comma and the name of the format to the variable name. For example, the following translation:

```json
{
  "foo": "I am {{age, number}} years old."
}
```

Will format the `age` variable as a number. The format name must be one of the formats that have been added to the
`Translator` (see the [Configuration](#configuration) section for more details).

Furthermore, one can also specify parameters for the format, by appending between parentheses after the format name.
Each parameter must be represented as a key-value pair, separated by a colon, and parameters must be separated by a
comma. For example, the following translation:

```json
{
  "foo": "I am {{age, number(minFracDigits: 2, maxFracDigits: 2)}} years old."
}
```

## Configuration

As said in the [builder section](#translatorbuilder), the `TranslatorBuilder` has a method to set the configuration of
the `Translator`: `TranslatorBuilder::withConfiguration`. This method takes in a `TranslatorConfiguration` object, which
can be built with the `TranslatorConfiguration::create` DSL builder.

Here is an example of a configuration with all the settings set to their default values (not setting the configuration,
or not setting a specific property will result in the default value being used):

```kt
val translator = Translator.builder(Locale.ENGLISH)
    ...
    .withConfiguration {
        interpolationDelimiter = VariableDelimiter.DEFAULT
        pluralMapper = Plural.Mapper.defaultMapper()
        formats = VariableFormatter.builtins()
        missingKeyPolicy = MissingKeyPolicy.THROW_EXCEPTION
    }
    .build()
```

### `interpolationDelimiter`

This property is used to set the symbols that will be used to delimit variables in translations. It is a
`VariableDelimiter`, which comes from the [Sprinkler Utils interpolation module](../utils/README.md#interpolation).

### `pluralMapper`

This property is used to set the `Plural.Mapper` that will be used to determine the plural form of a translation. It is
an interface with two methods which both take in a `Locale` and `Int` (which corresponds to the `count` option during
pluralization). Both of these methods must return a `Plural` enum value, which follow and represent the 
[CLDR plural rules](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html). That is to say,
the possible values are:
- `Plural.ZERO`
- `Plural.ONE`
- `Plural.TWO`
- `Plural.FEW`
- `Plural.MANY`
- `Plural.OTHER`

The two methods are `Plural.Mapper::mapPlural` and `Plural.Mapper::mapOrdinal`, which are used to determine the plural
form of a translation, and the ordinal form of a translation respectively. The difference between the two is that the
ordinal form is used when the translation is used to represent an ordinal number (for example, "1st", "2nd", "3rd", etc.)
instead of a cardinal number (for example, "1", "2", "3", etc.).

By default, the `Plural.Mapper.defaultMapper` method is used to create the `Plural.Mapper` object, which uses the
simplified [English plural rules](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html#en)

### `formats`

Formats are represented by the `VariableFormatter` interface, which has a single method: 
```kt 
fun format(value: Any, locale: Locale, extraArgs: List<Pair<String, String>>): String
```

This method takes in the value to format, the locale to use, and a list of extra arguments that can be used to format
the value. The list of extra arguments is a list of key-value pairs, where the key is the name of the argument, and the
value is the value of the argument.

Formats can be added to the `Translator` thanks to the `formats` property of the `TranslatorConfigurationBuilder`, which
is a `MutableMap<String, VariableFormatter>`. The key is the name of the format, and the value is the formatter itself.
This key will be used to retrieve the formatter when formatting a variable in a translation.

By default, the `VariableFormatter.builtins` method is used to create the `MutableMap<String, VariableFormatter>`
object, which contains the following formats:

- `number`: Formats a number. The value must be a subtype of `Number`. The extra arguments are:
  - `minIntDigits`: The minimum number of digits to use for the integer part of the number.
  - `maxIntDigits`: The maximum number of digits to use for the integer part of the number.
  - `minFracDigits`: The minimum number of digits to use for the fractional part of the number.
  - `maxFracDigits`: The maximum number of digits to use for the fractional part of the number.
  - `groupingUsed`: Whether to use grouping separators.
  - `roundingMode`: The rounding mode to use.

- `currency`: Formats a currency  (the currency symbol will be fetched from the `Currency` object corresponding to the
given locale.). The value must be a subtype of `Number`. It accepts the same extra arguments as the `number` format.

- `date`: Formats a date. The value must be a subtype of `TemporalAccessor`. The extra arguments are:
  - `dateStyle`: The style to use for the date.

- `time`: Formats a time. The value must be a subtype of `TemporalAccessor`. The extra arguments are:
  - `timeStyle`: The style to use for the time.

- `dateTime`: Formats a date and a time. The value must be a subtype of `TemporalAccessor`. The extra arguments are the
same as the `date` and `time` formats.


### `missingKeyPolicy`

This property is used to set the policy that will be used when a translation is not found for a given key. It is a
`MissingKeyPolicy` enum value, which can be one of the following:
- `MissingKeyPolicy.THROW_EXCEPTION`: throws an exception when a translation is not found for a given key.
- `MissingKeyPolicy.RETURN_KEY`: returns the key itself when a translation is not found for a given key.

By default, the `MissingKeyPolicy.THROW_EXCEPTION` value is used.

## Changelog

[Changelog](CHANGELOG.md)
