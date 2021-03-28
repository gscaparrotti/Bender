package com.github.gscaparrotti.bender.services;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Result<T> {

    final T value;
    final ResultType resultType;

    public Result(@Nullable final T value, @NonNull final ResultType resultType) {
        this.value = value;
        this.resultType = resultType;
    }

    public Result(@NonNull final ResultType resultType) {
        this(null, resultType);
    }

    public boolean hasValue() {
        return this.value != null;
    }

    @Nullable
    public T getValue() {
        return this.value;
    }

    @NonNull
    public ResultType getResultType() {
        return this.resultType;
    }


    public enum ResultType {

        OK(true), CREATED(true), NO_CONTENT(true),
        CONFLICT(false), BAD_REQUEST(false), ERROR(false);

        private final boolean isSuccess;

        ResultType(final boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public boolean isSuccess() {
            return this.isSuccess;
        }

        public boolean isError() {
            return !this.isSuccess;
        }

    }
}
