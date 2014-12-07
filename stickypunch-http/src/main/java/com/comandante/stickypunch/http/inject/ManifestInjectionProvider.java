package com.comandante.stickypunch.http.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.apache.log4j.Logger;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Provider
public class ManifestInjectionProvider implements InjectableProvider<ManifestInjectionContext, Type> {
    private static final Logger log = Logger.getLogger(ManifestInjectionProvider.class);
    private static final Map<Type, Injectable> manifest = new ConcurrentHashMap<Type, Injectable>();

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(final ComponentContext componentContext,
                                    final ManifestInjectionContext ctx,
                                    final Type type) {
        Injectable injectable = manifest.get(type);
        if (injectable == null) {
            log.error("injectable is null for " + type);
        }
        return injectable;
    }

    public static <T> void addToManifest(Class<T> type, final T obj) {
        manifest.put(type, new Injectable<T>() {
            @Override
            public T getValue() {
                return obj;
            }
        });
    }
}
