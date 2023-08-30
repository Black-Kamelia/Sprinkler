package com.kamelia.sprinkler.i18n

import com.kamelia.sprinkler.util.assertionFailed
import org.yaml.snakeyaml.Yaml
import java.nio.file.Path
import kotlin.io.path.inputStream


fun yamlParser(): I18nFileParser = I18nFileParser { Yaml().load(it.inputStream()) }

fun absoluteResource(vararg path: String): Path {
    require(path.isNotEmpty()) { "Path must not be empty" }
    val prefix = if (path[0].startsWith("/")) "" else "/"
    val actualPath = path.joinToString(separator = "/", prefix = prefix)
    val url = ResourceAccessor::class.java
        .getResource(actualPath)
        ?: assertionFailed("Resource $actualPath not found")
    return Path.of(url.toURI())
}

private sealed interface ResourceAccessor