package rocks.frieler.android.beans;

import java8.util.function.Supplier;

/**
 * Convenience-implementation for simple cases of an {@link ActivityScopedFactoryBean}.
 *
 * @author Christopher Frieler
 */
public class SimpleActivityScopedFactoryBean<T> extends ActivityScopedFactoryBean<T> {
    private final Class<T> type;
    private final Supplier<T> producer;

    public SimpleActivityScopedFactoryBean(Class<T> type, Supplier<T> producer) {
        this.type = type;
        this.producer = producer;
    }

    @Override
    protected Class<T> getType() {
        return type;
    }

    @Override
    protected T produceBean() {
        return producer.get();
    }
}
