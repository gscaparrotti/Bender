package com.github.gscaparrotti.bender.legacy;

import com.github.gscaparrotti.bender.services.Result;
import com.github.gscaparrotti.bender.springUtils.ApplicationContextProvider;
import java.util.function.Function;
import java.util.function.Supplier;

public class LegacyHelper {

    public static <T> T ctrl(final Class<T> clazz) {
        return ApplicationContextProvider.getApplicationContext().getBean(clazz);
    }

    public static <X, Y> Y ifBodyNotNull(final Result<X> result, final Function<X, Y> ifBodyNotNull, final Supplier<Y> orElse) {
        if (result.hasValue()) {
            return ifBodyNotNull.apply(result.getValue());
        }
        return orElse.get();
    }

    public static Supplier<Object> nullSupplier() {
        return () -> null;
    }

}
