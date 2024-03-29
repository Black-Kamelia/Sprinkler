package com.kamelia.sprinkler.bridge;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LambdaAdaptersTest {

    @Test
    public void wrapRunnable() {
        var list = new ArrayList<String>();
        var value = "hello";

        var f = LambdaAdapters.a(() -> list.add(value));
        f.invoke();

        assertEquals(List.of(value), list);
    }

    @Test
    public void wrapConsumer() {
        var list = new ArrayList<String>();
        var value = "hello";

        var f = LambdaAdapters.a((String s) -> list.add(s));
        f.invoke(value);

        assertEquals(List.of(value), list);
    }

    @Test
    public void wrapBiConsumer() {
        var list = new ArrayList<String>();
        var value1 = "hello";
        var value2 = "world";

        var f = LambdaAdapters.a((String s1, String s2) -> list.add(s1 + s2));
        f.invoke(value1, value2);

        assertEquals(List.of(value1 + value2), list);
    }

    @Test
    public void privateConstructorThrows() {
        var constructor = LambdaAdapters.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        var e = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, e.getTargetException().getClass());
    }

}
