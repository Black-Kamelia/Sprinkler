package com.kamelia.sprinkler.util.jvmlambda;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class to adapt Java lambdas to Kotlin lambdas.
 */
public final class LambdaAdapters {

    /**
     * Adapter fo a {@link Runnable} to a {@link Function0} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @return the wrapped lambda
     */
    public static Function0<Unit> a(Function0Adapter lambda) {
        Objects.requireNonNull(lambda, "lambda is null");
        return lambda;
    }

    /**
     * Adapter fo a {@link Consumer} to a {@link Function1} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @param <T>    the type of the argument
     * @return the wrapped lambda
     */
    public static <T> Function1<T, Unit> a(Function1Adapter<T> lambda) {
        Objects.requireNonNull(lambda, "lambda is null");
        return lambda;
    }

    /**
     * Adapter fo a {@link BiConsumer} to a {@link Function2} that returns {@link Unit}.
     *
     * @param lambda the lambda to wrap
     * @param <T1>   the type of the first argument
     * @param <T2>   the type of the second argument
     * @return the wrapped lambda
     */
    public static <T1, T2> Function2<T1, T2, Unit> a(Function2Adapter<T1, T2> lambda) {
        Objects.requireNonNull(lambda, "lambda is null");
        return lambda;
    }

    /**
     * A functional interface that adapts a {@link Runnable} to a {@link Function0}.
     */
    @FunctionalInterface
    public interface Function0Adapter extends Function0<Unit>, Runnable {

        @Override
        default Unit invoke() {
            run();
            return Unit.INSTANCE;
        }

    }

    /**
     * A functional interface that adapts a {@link Consumer} to a {@link Function1}.
     *
     * @param <T> the type of the argument
     */
    @FunctionalInterface
    public interface Function1Adapter<T> extends Function1<T, Unit>, Consumer<T> {

        @Override
        default Unit invoke(T t) {
            accept(t);
            return Unit.INSTANCE;
        }

    }

    /**
     * A functional interface that adapts a {@link BiConsumer} to a {@link Function2}.
     *
     * @param <T1> the type of the first argument
     * @param <T2> the type of the second argument
     */
    @FunctionalInterface
    public interface Function2Adapter<T1, T2> extends Function2<T1, T2, Unit>, BiConsumer<T1, T2> {

        @Override
        default Unit invoke(T1 t1, T2 t2) {
            accept(t1, t2);
            return Unit.INSTANCE;
        }

    }

    private LambdaAdapters() {
        throw new AssertionError("no instances");
    }

}
