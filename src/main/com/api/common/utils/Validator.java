package com.api.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Validator<T> {

    private final T obj;

    private final List<Throwable> exceptions = new ArrayList<>();

    private Validator(T obj) {
        this.obj = obj;
    }

    public static <T> Validator<T> of(T t) {
        return new Validator<>(Objects.requireNonNull(t));
    }

    public Validator<T> validate(Predicate<? super T> validation, RuntimeException exception) {
        if (!validation.test(obj)) {
            exceptions.add(exception);
        }
        return this;
    }

    public <U> Validator<T> validate(
            Function<? super T, ? extends U> projection,
            Predicate<? super U> validation,
            RuntimeException exception
    ) {
        return validate(projection.andThen(validation::test)::apply, exception);
    }

    public T get() throws Exception {
        if (exceptions.isEmpty()) {
            return obj;
        }
        RuntimeException e = (RuntimeException) exceptions.get(0);
        throw e;
    }
}
