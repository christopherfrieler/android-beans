package rocks.frieler.android.beans.scopes;

import java8.util.function.Supplier;

public class GenericScopedFactoryBean<T> implements ScopedFactoryBean<T> {
    private final String scope;
    private final Class<T> type;
    private final Supplier<T> producer;

    public GenericScopedFactoryBean(String scope, Class<T> type, Supplier<T> producer) {
        this.scope = scope;
        this.type = type;
        this.producer = producer;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public Class<T> getBeanType() {
        return type;
    }

    @Override
    public T produceBean() {
        return producer.get();
    }
}
