module com.black_kamelia.sprinkler.util {
    requires transitive kotlin.stdlib;
    requires transitive com.zwendo.restrikt.annotations;

    requires static org.jetbrains.annotations;

    exports com.kamelia.sprinkler.util;
}
