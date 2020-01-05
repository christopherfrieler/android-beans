package rocks.frieler.android.beans.scopes.singleton

import java8.util.function.Supplier
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean

/**
 * [ScopedFactoryBean] for beans of the [singleton][SingletonScopedFactoryBeanHandler.name]-scope.
 *
 *
 * There will be only a single instance of that bean, just as it would when defined without any
 * factory except that the bean will be instantiated lazily by the factory.
 */
class SingletonScopedFactoryBean<T>
private constructor(type: Class<T>, producer: Supplier<T>) : GenericScopedFactoryBean<T>(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE, type, producer) {

    companion object {
        /**
         * Creates a new [SingletonScopedFactoryBean] to produce a bean of the given type using the given producer.
         *
         *
         * Since beans are singletons by default, this has the effect of a lazy instantiation.
         *
         * @param type the type of bean produced
         * @param producer the producer to create new beans
         * @param <T> the type of bean produced
         * @return a new [SingletonScopedFactoryBean]
         */
        @JvmStatic
        fun <T> lazy(type: Class<T>, producer: Supplier<T>): SingletonScopedFactoryBean<T> {
            return SingletonScopedFactoryBean(type, producer)
        }
    }
}
