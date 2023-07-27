package com.kamelia.sprinkler.util.jvmlambda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class KotlinDslAdapterTest {

    @Test
    public void testAdapter() {
        var name = "John";
        var age = 30;

        var person = DummyPerson.Companion.create(b ->
            b.name(name)
                .age(age)
                .finish()
        );

        assertEquals(name, person.getName());
        assertEquals(age, person.getAge());
    }

}
