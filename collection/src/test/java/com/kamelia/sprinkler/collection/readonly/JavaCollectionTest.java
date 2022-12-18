package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaCollectionTest {

    @Test
    public void readOnlyCollectionDoesNotAllowMutationMethods() {
        var collection = (Collection<Integer>) new ArrayList<>(List.of(1, 2, 3));
        var readOnlyCollection = ReadOnlyUtils.toReadOnlyCollection(collection);

        assertThrows(NullPointerException.class, () -> readOnlyCollection.add(4));
        assertThrows(NullPointerException.class, () -> readOnlyCollection.remove(0));
        assertThrows(NullPointerException.class, () -> readOnlyCollection.addAll(List.of(4, 5, 6)));
        assertThrows(NullPointerException.class, () -> readOnlyCollection.removeAll(List.of(1, 2, 3)));
        assertThrows(NullPointerException.class, () -> readOnlyCollection.retainAll(List.of(1, 2, 3)));
        assertThrows(NullPointerException.class, readOnlyCollection::clear);
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var collection = (Collection<Integer>) new ArrayList<>(List.of(1, 2, 3));
        var readOnlyCollection = ReadOnlyUtils.toReadOnlyCollection(collection);
        var iterator = readOnlyCollection.iterator();

        assertThrows(NullPointerException.class, iterator::remove);
    }

}
