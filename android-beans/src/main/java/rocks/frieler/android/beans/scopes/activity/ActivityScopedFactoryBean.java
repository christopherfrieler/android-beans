package rocks.frieler.android.beans.scopes.activity;

import java8.util.function.Supplier;
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBean;

/**
 * {@link ScopedFactoryBean} for beans of the {@link ActivityScopedFactoryBeanHandler#ACTIVITY_SCOPE}-scope.
 */
public class ActivityScopedFactoryBean<T> extends GenericScopedFactoryBean<T> {

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

    private ActivityScopedFactoryBean(Class<T> type, Supplier<T> producer) {
        super(ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE, type, producer);
    }
}
