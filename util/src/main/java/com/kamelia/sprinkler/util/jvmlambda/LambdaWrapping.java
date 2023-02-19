package com.kamelia.sprinkler.util.jvmlambda;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class for wrapping lambdas in Java to Kotlin lambdas.
 */
public final class LambdaWrapping {

    /**
     * Wraps a {@link Runnable} in a {@link Function0} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @return the wrapped lambda
     */
    public static Function0<Unit> w(Runnable lambda) {
        Objects.requireNonNull(lambda, "lambda");
        return () -> {
            lambda.run();
            return Unit.INSTANCE;
        };
    }

    /**
     * Wraps a {@link Consumer} in a {@link Function1} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @return the wrapped lambda
     * @param <T> the type of the argument
     */
    public static <T> Function1<T, Unit> w(Consumer<T> lambda) {
        Objects.requireNonNull(lambda, "lambda");
        return (t) -> {
            lambda.accept(t);
            return Unit.INSTANCE;
        };
    }

    /**
     * Wraps a {@link BiConsumer} in a {@link Function2} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @return the wrapped lambda
     * @param <T1> the type of the first argument
     * @param <T2> the type of the second argument
     */
    public static <T1, T2> Function2<T1, T2, Unit> w(BiConsumer<T1, T2> lambda) {
        Objects.requireNonNull(lambda, "lambda");
        return (t1, t2) -> {
            lambda.accept(t1, t2);
            return Unit.INSTANCE;
        };
    }

    private LambdaWrapping() {
        throw new AssertionError("no instances");
    }

}
