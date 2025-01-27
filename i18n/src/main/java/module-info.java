module com.black_kamelia.sprinkler.i18n {
    requires transitive kotlin.stdlib;
    requires transitive com.zwendo.restrikt.annotations;
    requires transitive com.black_kamelia.sprinkler.util;

    requires static org.jetbrains.annotations;
    requires static com.fasterxml.jackson.databind;
    requires static com.google.gson;
    requires static org.json;
    requires static org.yaml.snakeyaml;
    requires static com.fasterxml.jackson.dataformat.yaml;
    requires static com.fasterxml.jackson.dataformat.toml;
    requires static toml4j;

    exports com.kamelia.sprinkler.i18n;
    exports com.kamelia.sprinkler.i18n.formatting;
    exports com.kamelia.sprinkler.i18n.pluralization;
}
