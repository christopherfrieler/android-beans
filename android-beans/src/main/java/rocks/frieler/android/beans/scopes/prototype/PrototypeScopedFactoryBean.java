package rocks.frieler.android.beans.scopes.prototype;

import java8.util.function.Supplier;
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBean;

import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE;

/**
 * {@link ScopedFactoryBean} for beans of the {@link PrototypeScopedFactoryBeanHandler#PROTOTYPE_SCOPE}-scope.
 */
public class PrototypeScopedFactoryBean<T> extends GenericScopedFactoryBean<T> {

    /**
     * Creates a new {@link PrototypeScopedFactoryBean} to produce a bean of the given type using the given producer.
     *
     * @param type the type of bean produced
     * @param producer the producer to create new beans
     * @param <T> the type of bean produced
     * @return a new {@link PrototypeScopedFactoryBean}
     */
    public static <T> PrototypeScopedFactoryBean<T> prototype(Class<T> type, Supplier<T> producer) {
        return new PrototypeScopedFactoryBean<>(type, producer);
    }

    private PrototypeScopedFactoryBean(Class<T> type, Supplier<T> producer) {
        super(PROTOTYPE_SCOPE, type, producer);
    }
}
