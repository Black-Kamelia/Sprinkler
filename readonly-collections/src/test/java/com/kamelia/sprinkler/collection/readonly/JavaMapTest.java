package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaMapTest {

    @Test
    public void readOnlyMapDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);

        assertThrows(UnsupportedOperationException.class, () -> readOnlyMap.put("key", "value"));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyMap.remove("key"));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyMap.putAll(Map.of("key", "value")));
        assertThrows(UnsupportedOperationException.class, readOnlyMap::clear);
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var iterator = readOnlyMap.entrySet().iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlyEntryDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var iterator = readOnlyMap.entrySet().iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlyMapEntriesSetDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var entries = readOnlyMap.entrySet();

        assertThrows(UnsupportedOperationException.class, () -> entries.add(Map.entry("key", "value")));
        assertThrows(UnsupportedOperationException.class, () -> entries.remove(Map.entry("key", "value")));
        assertThrows(UnsupportedOperationException.class, () -> entries.addAll(Map.of("key", "value").entrySet()));
        assertThrows(UnsupportedOperationException.class, () -> entries.removeAll(Map.of("key", "value").entrySet()));
        assertThrows(UnsupportedOperationException.class, () -> entries.retainAll(Map.of("key", "value").entrySet()));
        assertThrows(UnsupportedOperationException.class, entries::clear);
    }

    @Test
    public void readOnlyMApEntriesSetIteratorDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var iterator = readOnlyMap.entrySet().iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlyMapKeysSetDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var keys = readOnlyMap.keySet();

        assertThrows(UnsupportedOperationException.class, () -> keys.add("key"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove("key"));
        assertThrows(UnsupportedOperationException.class, () -> keys.addAll(List.of("key1", "key2")));
        assertThrows(UnsupportedOperationException.class, () -> keys.removeAll(List.of("key1", "key2")));
        assertThrows(UnsupportedOperationException.class, () -> keys.retainAll(List.of("key1", "key2")));
        assertThrows(UnsupportedOperationException.class, keys::clear);
    }

    @Test
    public void readOnlyMapKeysSetIteratorDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var iterator = readOnlyMap.keySet().iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlyMapValuesCollectionDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var values = readOnlyMap.values();

        assertThrows(UnsupportedOperationException.class, () -> values.add("value"));
        assertThrows(UnsupportedOperationException.class, () -> values.remove("value"));
        assertThrows(UnsupportedOperationException.class, () -> values.addAll(List.of("value1", "value2")));
        assertThrows(UnsupportedOperationException.class, () -> values.removeAll(List.of("value1", "value2")));
        assertThrows(UnsupportedOperationException.class, () -> values.retainAll(List.of("value1", "value2")));
        assertThrows(UnsupportedOperationException.class, values::clear);
    }

    @Test
    public void readOnlyMapValuesCollectionIteratorDoesNotAllowMutationMethods() {
        var map = new HashMap<String, String>();
        var readOnlyMap = ReadOnlyUtils.toReadOnlyMap(map);
        var iterator = readOnlyMap.values().iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

}
