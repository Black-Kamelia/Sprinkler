package com.kamelia.sprinkler.i18n.impl

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.dataformat.toml.TomlStreamReadException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.kamelia.sprinkler.util.illegalState
import com.kamelia.sprinkler.util.unsafeCast
import com.moandjiezana.toml.Toml
import com.zwendo.restrikt2.annotation.PackagePrivate
import org.json.JSONException
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException


/**
 * We must use anonymous objects instead of lambdas because dependencies are optional. Lambdas will trigger eager
 * resolution of the dependencies when this class is loaded, which will cause a NoClassDefFoundError if the dependency
 * is not present. Because anonymous objects are different classes, they are only loaded when they are actually used.
 */
@PackagePrivate
@Suppress("ObjectLiteralToLambda")
internal object BuiltinContentParsers {

    fun jsonParser(): () -> TranslatorBuilder.ContentParser =
        compositeParser(listOf(BuiltinContentParsers::jacksonDatabind, BuiltinContentParsers::gson, BuiltinContentParsers::json), "JSON")

    fun yamlParser(): () -> TranslatorBuilder.ContentParser =
        compositeParser(listOf(BuiltinContentParsers::jacksonYaml, BuiltinContentParsers::snakeYaml), "YAML")

    fun tomlParser(): () -> TranslatorBuilder.ContentParser = compositeParser(listOf(BuiltinContentParsers::jacksonToml, BuiltinContentParsers::toml4j), "TOML")

    fun compositeParser(
        candidates: List<() -> TranslatorBuilder.ContentParser>,
        format: String,
    ): () -> TranslatorBuilder.ContentParser {
        val lazyValue = lazy(LazyThreadSafetyMode.NONE) {
            for (candidate in candidates) {
                try {
                    return@lazy candidate.invoke()
                } catch (e: NoClassDefFoundError) {
                    // Ignore
                }
            }
            illegalState("No available content parser found for '$format' format. You might need to add a dependency to your project to allow loading. You can check the possible dependencies in the TranslatorBuilder.defaultContentLoaders method documentation.")
        }
        return { lazyValue.value }
    }

    //region json
    fun jacksonDatabind(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap {
            return try {
                ObjectMapper().readValue(content, Map::class.java).unsafeCast()
            } catch (e: JsonParseException) {
                throw IllegalArgumentException("Invalid JSON file.", e)
            }
        }
    }

    private fun gson(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            Gson().fromJson(content, object : TypeToken<TranslationSourceMap>() {})
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON file.", e)
        }
    }

    private fun json(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            JSONObject(content).toMap()
        } catch (e: JSONException) {
            throw IllegalArgumentException("Invalid JSON file.", e)
        }

    }
    //endregion

    //region yaml
    private fun snakeYaml(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            Yaml().load(content)
        } catch (e: YAMLException) {
            throw IllegalArgumentException("Invalid YAML file.", e)
        }
    }

    private fun jacksonYaml(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            ObjectMapper(YAMLFactory()).readValue(content, Map::class.java).unsafeCast()
        } catch (e: MismatchedInputException) {
            throw IllegalArgumentException("Invalid YAML file.", e)
        }
    }
    //endregion

    //region toml
    private fun toml4j(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            Toml().read(content).toMap()
        } catch (e: IllegalStateException) {
            throw IllegalArgumentException("Invalid TOML file.", e)
        }
    }

    private fun jacksonToml(): TranslatorBuilder.ContentParser = object : TranslatorBuilder.ContentParser {
        override fun load(content: String): TranslationSourceMap = try {
            ObjectMapper(TomlFactory()).readValue(content, Map::class.java).unsafeCast()
        } catch (e: TomlStreamReadException) {
            throw IllegalArgumentException("Invalid TOML file.", e)
        }
    }
    //endregion

    //region testing
    fun jsonParsers(): List<TranslatorBuilder.ContentParser> = listOf(jacksonDatabind(), gson(), json())

    fun yamlParsers(): List<TranslatorBuilder.ContentParser> = listOf(snakeYaml(), jacksonYaml())

    fun tomlParsers(): List<TranslatorBuilder.ContentParser> = listOf(jacksonToml(), toml4j())
    //endregion

}
