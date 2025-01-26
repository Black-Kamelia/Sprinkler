# How to (Sprinkler i18n)

## Table of contents

<!-- TOC -->
* [How to (Sprinkler i18n)](#how-to-sprinkler-i18n)
  * [Table of contents](#table-of-contents)
  * [Introduction](#introduction)
  * [Create a translator](#create-a-translator)
    * [Filled directly from code](#filled-directly-from-code)
    * [Filled using files](#filled-using-files)
    * [Filled using resources](#filled-using-resources)
  * [Translate (basic)](#translate-basic)
    * [Passing the locale explicitly (to the translation method)](#passing-the-locale-explicitly-to-the-translation-method)
    * [Using the current locale of the translator](#using-the-current-locale-of-the-translator)
    * [With a fallback locale](#with-a-fallback-locale)
    * [With sections](#with-sections)
    * [With fallback keys](#with-fallback-keys)
  * [Translate (advanced)](#translate-advanced)
    * [Interpolation](#interpolation)
    * [Contextualization](#contextualization)
    * [Pluralization (cardinal)](#pluralization-cardinal)
    * [Pluralization (ordinal)](#pluralization-ordinal)
    * [Pluralization and Contextualization](#pluralization-and-contextualization)
  * [Formatting (using builtin formatters)](#formatting-using-builtin-formatters)
    * [Basic formatting](#basic-formatting)
    * [With parameters (passed in the translation string)](#with-parameters-passed-in-the-translation-string)
    * [With parameters (passed to the translation method)](#with-parameters-passed-to-the-translation-method)
  * [Configure the translator more in depth](#configure-the-translator-more-in-depth)
    * [Set custom interpolation delimiters](#set-custom-interpolation-delimiters)
    * [Add a custom formatter](#add-a-custom-formatter)
    * [Set a custom locale parser](#set-a-custom-locale-parser)
<!-- TOC -->

## Introduction

This file serves as a guide to meeting individual common needs using the Sprinkler i18n library. Each section provides a
concise and straightforward explanation of how to achieve a specific goal, along with a corresponding code example. 

## Create a translator

### Filled directly from code

To create a Translator, you need to use the `Translator` companion `invoke` method. This method is the entry point 
of the translator creation DSL. From there, you can open a `translations` block, where you can add translations. 

While not being the usual way to add translations, a translator can be filled directly from code, by using the `map`
method. The map accepts a locale (the language of the translations), and a map (the translations themselves).

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH, 
            mapOf(
                "greetings" to "Hello, world!",
                "farewell" to "Goodbye, world!",
            )
        )
        map(
            Locale.FRENCH, 
            mapOf(
                "greetings" to "Bonjour, le monde !",
                "farewell" to "Au revoir, le monde !",
            )
        )
    }
}
```

### Filled using files

To create a Translator, you need to use the `Translator` companion `invoke` method. This method is the entry point
of the translator creation DSL. From there, you can open a `translations` block, where you can add translations.

One of the most common way to add translations is by using files. The  `file` methods allow you to add files to the
translator. The files can be added by providing their path (using a `Path` object), either representing a file or a
directory. If a directory is provided, the translator will scan for files in the directory (only the first level of
depth).

```kt
val translator = Translator {
    translations {
        // You can add files explicitly by providing their path
        file(Path("path", "to", "translations", "en.json")) // using Path
        
        // And you can also add directories. It will scan for files in the directory
        file(Path("path", "to", "translations"))
    }
}
```

### Filled using resources

To create a Translator, you need to use the `Translator` companion `invoke` method. This method is the entry point
of the translator creation DSL. From there, you can open a `translations` block, where you can add translations.

Another common way to add translations is by using resources. The `resource` method allows you to add resources (files
stored in the `resources` directory of a project or in a jar file). The resources can be added by providing their path
(using a `String` object), either representing a file or a directory. If a directory is provided, the translator will
scan for files in the directory (only the first level of depth).

When working with resources, paths can be absolute or relative. If the path is relative, it is resolved in the same way
as `java.lang.Class.getResource(String)` method, the package of a class is used as a base path. The class to use can be
specified either explicitly (by passing it to the `resource` method) or implicitly, by using the class in which the`invoke` method has been called.

```kt
package com.example

object Foo {
    val translator = Translator {
        translations {
            // The path is absolute, it will be resolved without further modification
            resource("/path/to/translations/en.json") // actual path: /path/to/translations/en.json

            // This path is relative, it will be resolved using the package of the calling class, here com.example
            resource("path/to/translations") // actual path: /com/example/path/to/translations
        }
    }    
}
```

## Translate (basic)

### Passing the locale explicitly (to the translation method)

Once your translator is created, you can use it to translate strings. To do so, you need to call the `t` method. This
method accepts the key of the translation to use, and an arbitrary number of `TranslationArgument` objects.

The `selectedLocale` is one of the `TranslationArgument` objects. It allows you to specify the locale to use for the
translation.

```kt
val translator: Translator = myFunctionToCreateATranslator() // (of course this is not a function from the library)
/* 
    containing:
    { 
        "en": { "greetings": "Hello, world!" },
        "fr": { "greetings": "Bonjour, le monde !" }
    }
*/

println(translator.t("greetings", selectedLocale(Locale.ENGLISH))) // prints "Hello, world!"
```

### Using the current locale of the translator

Once your translator is created, you can use it to translate strings. To do so, you need to call the `t` method. This
method accepts the key of the translation to use, and an arbitrary number of `TranslationArgument` objects.

When calling the `t` method, unless specified explicitly, the current locale of the translator is used as the language
of the translation. To change the current locale of the translator, you can use the `withNewCurrentLocale` method, which
creates a new translator with the specified locale as the current locale.

```kt
val translator: Translator = myFunctionToCreateATranslator() // (of course this is not a function from the library)
/* 
    containing:
    { 
        "en": { "greetings": "Hello, world!" },
        "fr": { "greetings": "Bonjour, le monde !" }
    }
*/

// When creating a translator, the current locale is set to its default locale, which by default is Locale.ENGLISH.
println(translator.t("greetings")) // prints "Hello, world!"

// You can change the current locale of the translator
val frenchTranslator: Translator = translator.withNewCurrentLocale(Locale.FRENCH)
println(frenchTranslator.t("greetings")) // prints "Bonjour, le monde !"

// Note that withNewCurrentLocale creates a new translator, but the original translator is not modified
println(translator.t("greetings")) // Still prints "Hello, world!"
```

### With a fallback locale

When translating a string, you can provide a fallback locale, in case the translation is not found. The `fallbackLocale`
argument is the `TranslationArgument` allowing you to specify the fallback locale.

```kt
val translator: Translator = myFunctionToCreateATranslator() // (of course this is not a function from the library)
/* 
    containing:
    { 
        "en": { "greetings": "Hello, world!" },
        "fr": { "greetings": "Bonjour, le monde !" }
    }
*/

// When translating, you can provide a fallback locale, in case the translation is not found.
println(
    translator.t(
        "greetings", 
        selectedLocale(Locale.GERMAN),
        fallbackLocale(Locale.FRENCH),
    )
) // prints "Bonjour, le monde !"

// If no fallback locale is provided, the default locale is used (here Locale.ENGLISH)
println(translator.t("greetings", selectedLocale(Locale.GERMAN))) // prints "Hello, world!"
```

### With sections

When working with a translator and the `t` method, you usually need to provide the full path of a nested translation
key. However, because it can be tedious to always specify the full path, you can use sections. Sections (accessible
through the `section` method) allow you to create a new translator, with a base path, and then use this translator to
translate strings. 

```kt
val translator: Translator = myFunctionToCreateATranslator() // (of course this is not a function from the library)
/* 
    containing:
    { 
        "en": { 
            "error": { 
                "not_found": "The resource was not found.",
                "forbidden": "You are not allowed to access this resource."
            },
            "key": "value"
        },
    }
*/

val errorTranslator = translator.section("error")

println(errorTranslator.t("not_found")) // prints "The resource was not found."
println(errorTranslator.t("forbidden")) // prints "You are not allowed to access this resource."

// The original translator is not modified
println(translator.t("key")) // prints "value"
```

### With fallback keys

When translating a string, you can provide a list of fallback keys, in case the key is not found. The `fallbacks`
argument is the `TranslationArgument` allowing you to specify the fallback keys (as a vararg of `String`). If the passed
key is not found, the translator will try to use the fallback keys in order. In the case where none of the fallback keys
are found, the translator will try to use the fallback locale (if provided).

```kt
import java.util.Locale

val translator: Translator = myFunctionToCreateATranslator() // (of course this is not a function from the library)
/* 
    containing:
    { 
        "en": { 
            "error": {
                "default": "An error occurred.",
                "not_found": "The resource was not found.",
            }
        },
        "fr": { 
            "error": {
                "default": "Une erreur est survenue.",
            }
        }
    }
*/

// You can provide a list of fallback keys, in case the key is not found
println(
    translator.t(
        "error.not_found", 
        selectedLocale(Locale.FRENCH), 
        fallbacks("error.default")),
) // prints "Une erreur est survenue."
// You can notice that the fallback key has been used before trying to use the default locale
```

## Translate (advanced)

### Interpolation

Interpolation is a way to insert variables into a translation string. To interpolate a variable, you need to declare it
in the translation string by placing it between specific characters called variable delimiters. By default, the variable
delimiters are `{{` and `}}`.

Once your variable is declared in the translation string, you can pass its value to the `t` method by using the
`variable` `TranslationArgument`. This argument takes the name of the variable and its value.

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH, 
            mapOf(
                "greetings" to "Hello, {{name}}!",
            )
        )
    }
}

println(translator.t("greetings", variable("name", "John"))) // prints "Hello, John!"
```

### Contextualization

A common issue when translating strings is that the same key can have different translations depending on the context.
To solve this issue, you can use contextualization. Contextualization can be achieved by using the `context` method.
This method allows you to specify the context of the translation.

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH, 
            mapOf(
                "child_male" to "He is my son.",
                "child_female" to "She is my daughter.",
            )
        )
    }
}

println(translator.t("child", context("male"))) // prints "He is my son."
println(translator.t("child", context("female"))) // prints "She is my daughter."
```

### Pluralization (cardinal)

Pluralization is a way to handle different translations depending on the cardinality of a variable. To perform
pluralization, you first need to add each translation variant of your string to the translator (plural is appended to
the base key with an underscore). Then, you can use the `count` `TranslationArgument` to specify the cardinality of the
variable.

The correct plural form is computed by the translator using a `Plural`

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH, 
            mapOf(
                "child_one" to "I have 1 child.",
                "child_other" to "I have {{count}} children.",
            )
        )
    }
}

println(translator.t("child", count(1))) // prints "I have one child."
println(translator.t("child", count(3))) // prints "I have 3 children."
```

### Pluralization (ordinal)

In addition to cardinal pluralization, you sometimes need to handle ordinal pluralization. Ordinal pluralization is a
way to handle different translations depending on the ordinality of a variable. To perform ordinal pluralization, you
first need to add each translation variant of your string to the translator (ordinal is appended to the base key with an
underscore, followed by the plural form). Then, you can use the `count` `TranslationArgument` to specify the ordinality
in combination with the `ordinal` `TranslationArgument`.

The correct ordinal plural form is computed by the translator using a `Plural`

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH,
            mapOf(
                "date_ordinal_one" to "Today is {{count}}st day of the month.",
                "date_ordinal_two" to "Today is {{count}}nd day of the month.",
                "date_ordinal_few" to "Today is {{count}}rd day of the month.",
                "date_ordinal_other" to "Today is {{count}}th day of the month.",
            )
        )
    }
}

println(translator.t("date", count(1), ordinal())) // prints "Today is 1st day of the month."
println(translator.t("date", count(23), ordinal())) // prints "Today is 23rd day of the month."
```

### Pluralization and Contextualization

Pluralization and contextualization can be combined to handle different translations depending on the cardinality and
context of a variable. To perform pluralization and contextualization, you first need to add each translation variant of
your string to the translator (the context is appended to the base key with an underscore, followed by the plural form).
Then, you can use the `count` `TranslationArgument` to specify the cardinality of the variable, and the `context`
`TranslationArgument` to specify the context of the translation.

```kt
val translator = Translator {
    translations {
        map(
            Locale.ENGLISH, 
            mapOf(
                "child_male_one" to "I have 1 son.",
                "chile_male_other" to "I have {{count}} sons.",
                "child_female_one" to "I have 1 daughter.",
                "child_female_other" to "I have {{count}} daughters.",
            )
        )
    }
}

println(translator.t("child", count(1), context("female"))) // prints "I have one daughter."
println(translator.t("child", count(3), context("male"))) // prints "I have 3 sons."
```

## Formatting (using builtin formatters)

### Basic formatting

The library provides a set of built-in formatters that can be used to format variables. To format a variable, you need
to declare it right after the variable name declaration in the translation string. The format is declared, after the
name of the variable, separated a `,` (comma), by the name of the formatter.

```kt
val translator = Translator {
    configuration {
        currentLocale = Locale.US
    }
    translations {
        map(
            Locale.US, 
            mapOf(
                "money" to "I have {{amount, currency}}.",
            )
        )
    }
}

println(translator.t("money", variable("amount", 12.4))) // prints "I have $12.40."
```

### With parameters (passed in the translation string)

Formatters can perform complex formatting, and accept parameters to customize this formatting. To pass parameters to a
formatter, place them in parentheses after the formatter name. The parameters are a list of key-value pairs, where keys
and values are separated by a `:` (colon), and pairs are separated by a `,` (comma). The parameters are passed to the
formatter when the translation is performed.

```kt
val translator = Translator {
    translation {
        currentLocale = Locale.US
    }
    translations {
        map(
            Locale.US, 
            mapOf(
                "money" to "I have {{amount, currency(minFracDigits:3))}}.",
            )
        )
    }
}

println(translator.t("money", variable("amount", 12.4))) // prints "I have $12.400."
```

### With parameters (passed to the translation method)

Passing parameters through the translation string is convenient, but sometimes you need to pass parameters with a value
that is determined at runtime. To do so, you can pass parameters to the formatter when calling the `t` method. When
using the `variable` `TranslationArgument`, you can use the `formatArgument` method to pass parameters to the formatter.

```kt
val translator = Translator {
    configuation {
        currentLocale = Locale.US
    }
    translations {
        map(
            Locale.US, 
            mapOf(
                "money" to "I have {{amount, currency}}.",
            )
        )
    }
}

println(
    translator.t(
        "money",
        variable("amount", 12.4, formatArgument("minFracDigits", 3)),
    )
) // prints "I have $12.400."
```

## Configure the translator more in depth

### Set custom interpolation delimiters

By default, the interpolation delimiters are `{{` and `}}`. You can change them by using the `interpolationDelimiter`
property in the configuration block. This property takes an `InterpolationDelimiter` object, which is a simple class
containing the start and end delimiters.

```kt
val translator = Translator {
    configuration {
        interpolationDelimiter = TranslatorBuilder.interpolatioDelimiter("{", "}")
    }
    translations {
        map(
            Locale.ENGLISH,
            mapOf(
                "greetings" to "Hello, {name}!",
            )
        )
    }
}

println(translator.t("greetings", variable("name", "John"))) // prints "Hello, John!"
```

### Add a custom formatter

To add a custom formatter, you need to use the `formatters` property in the configuration block. This property is a
map associating a name to a `VariableFormatter` object.

```kt
val translator = Translator {
    configuration {
        formatters += "my-formatter" to VariableFormatter<Any> { a, v, _, _ ->
            // a formatter that simply repeats the value twice
            a.append(v.toString())
            a.append(v.toString())
        }
    }
    translations {
        map(Locale.ENGLISH, mapOf("custom" to "Hey {{value, my-formatter}}!"))
    }
}

println(translator.t("custom", variable("value", "world"))) // prints "Hey worldworld!"
```

### Set a custom locale parser

By default, the library uses the `Locale.forLanguageTag` method to parse locale strings from file names. You can change
this behavior by using the `localeParser` property in the translations block. This property takes a lambda that accepts
a string and returns a `Locale` object.

```kt
val translator = Translator {
    translations {
        localeParser = {
            when (it) {
                "ork" -> Locale.forLanguageTag("ork")
                "elf" -> Locale.forLanguageTag("elf")
                "dwf" -> Locale.forLanguageTag("dwf")
                else -> TranslatorBuilder.defaultLocaleParser(it)
            }
        }
    }
}
```
