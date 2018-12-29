package rocks.frieler.android.beans.scopes.activity;

import java8.util.function.Supplier;
import rocks.frieler.android.beans.scopes.ScopedFactoryBean;

/**
 * {@link ScopedFactoryBean} for beans of the {@link ActivityScopedFactoryBeanHandler#ACTIVITY_SCOPE}-scope.
 */
public class ActivityScopedFactoryBean<T> implements ScopedFactoryBean<T> {

    /**
     * Creates a new {@link ActivityScopedFactoryBean} to produce a bean of the given type using the given producer.
     *
     * @param type the type of bean produced
     * @param producer the producer to create new beans
     * @param <T> the type of bean produced
     * @return a new {@link ActivityScopedFactoryBean}
     */
    public static <T> ActivityScopedFactoryBean<T> activityScoped(Class<T> type, Supplier<T> producer) {
        return new ActivityScopedFactoryBean<>(type, producer);
    }

    private final Class<T> type;
    private final Supplier<T> producer;

    private ActivityScopedFactoryBean(Class<T> type, Supplier<T> producer) {
        this.type = type;
        this.producer = producer;
    }

    @Override
    public String getScope() {
        return ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE;
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
