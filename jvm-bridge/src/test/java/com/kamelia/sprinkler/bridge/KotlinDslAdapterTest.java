package com.kamelia.sprinkler.bridge;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class KotlinDslAdapterTest {

    class Foo implements KotlinDslAdapter {

    }

    @Test
    public void testAdapter() {
        Assertions.assertDoesNotThrow(() -> {
            Foo.class.getMethod("unit");
        });
    }

}
