package rocks.frieler.android.beans.scopes.singleton;

import java8.util.function.Supplier;
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBean;

import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE;

/**
 * {@link ScopedFactoryBean} for beans of the {@link SingletonScopedFactoryBeanHandler#SINGLETON_SCOPE}-scope.
 */
public class SingletonScopedFactoryBean<T> extends GenericScopedFactoryBean<T> {

    /**
     * Creates a new {@link SingletonScopedFactoryBean} to produce a bean of the given type using the given producer.
     * <p>
     * Since beans are singletons by default, this has the effect of a lazy instantiation.
     *
     * @param type the type of bean produced
     * @param producer the producer to create new beans
     * @param <T> the type of bean produced
     * @return a new {@link SingletonScopedFactoryBean}
     */
    public static <T> SingletonScopedFactoryBean<T> lazy(Class<T> type, Supplier<T> producer) {
        return new SingletonScopedFactoryBean<>(type, producer);
    }

    private SingletonScopedFactoryBean(Class<T> type, Supplier<T> producer) {
        super(SINGLETON_SCOPE, type, producer);
    }
}
