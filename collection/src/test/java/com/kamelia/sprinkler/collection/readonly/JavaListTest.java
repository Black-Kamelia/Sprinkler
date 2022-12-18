package com.kamelia.sprinkler.collection.readonly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JavaListTest {

    @Test
    public void readOnlyListDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);

        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.add(4));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.set(0, 4));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.addAll(List.of(4, 5, 6)));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.removeAll(List.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, () -> readOnlyList.retainAll(List.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, readOnlyList::clear);
    }

    @Test
    public void readOnlyIteratorDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);
        var iterator = readOnlyList.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlyListIteratorDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);
        var iterator = readOnlyList.listIterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> iterator.add(4));
        assertThrows(UnsupportedOperationException.class, () -> iterator.set(4));
    }

    @Test
    public void readOnlyListSubListDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);
        var subList = readOnlyList.subList(0, 1);

        assertThrows(UnsupportedOperationException.class, () -> subList.add(4));
        assertThrows(UnsupportedOperationException.class, () -> subList.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> subList.set(0, 4));
        assertThrows(UnsupportedOperationException.class, () -> subList.addAll(List.of(4, 5, 6)));
        assertThrows(UnsupportedOperationException.class, () -> subList.removeAll(List.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, () -> subList.retainAll(List.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, subList::clear);
    }

    @Test
    public void readOnlySubListIteratorDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);
        var subList = readOnlyList.subList(0, 1);
        var iterator = subList.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void readOnlySubListListIteratorDoesNotAllowMutationMethods() {
        var list = new ArrayList<>(List.of(1, 2, 3));
        var readOnlyList = ReadOnlyUtils.toReadOnlyList(list);
        var subList = readOnlyList.subList(0, 1);
        var iterator = subList.listIterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertThrows(UnsupportedOperationException.class, () -> iterator.add(4));
        assertThrows(UnsupportedOperationException.class, () -> iterator.set(4));
    }

}
