package org.knowm.xchange.interceptor;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.concurrent.atomic.AtomicReference;
import si.mazi.rescu.Interceptor;

public class InterceptorProvider {

    private static final Supplier<Collection<Interceptor>> INTERCEPTORS_SUPPLIER =
            new Supplier<>() {
                private final AtomicReference<Collection<Interceptor>> cache = new AtomicReference<>();

                @Override
                public Collection<Interceptor> get() {
                    Collection<Interceptor> result = cache.get();
                    if (result == null) {
                        synchronized (cache) {
                            result = cache.get();
                            if (result == null) {
                                final ServiceLoader<Interceptor> serviceLoader = ServiceLoader.load(Interceptor.class);
                                result = StreamSupport.stream(serviceLoader.spliterator(), false)
                                        .collect(Collectors.toSet());
                                cache.set(result);
                            }
                        }
                    }
                    return result;
                }
            };

    public static Collection<Interceptor> provide() {
        return INTERCEPTORS_SUPPLIER.get();
    }
}
