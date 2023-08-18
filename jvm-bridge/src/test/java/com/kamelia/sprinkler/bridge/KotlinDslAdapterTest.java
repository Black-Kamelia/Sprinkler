package com.kamelia.sprinkler.bridge;

import kotlin.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class KotlinDslAdapterTest {

    class Foo implements KotlinDslAdapter {

    }

    @Test
    public void assertsThatMethodDoesExist() {
        Assertions.assertDoesNotThrow(() -> {
            Foo.class.getMethod("unit");
        });
    }

    @Test
    public void assertsThatMethodDoesReturnUnit() {
        Assertions.assertDoesNotThrow(() -> {
            var method = Foo.class.getMethod("unit");
            var result = method.invoke(new Foo());
            Assertions.assertEquals(Unit.INSTANCE, result);
        });
    }

}
