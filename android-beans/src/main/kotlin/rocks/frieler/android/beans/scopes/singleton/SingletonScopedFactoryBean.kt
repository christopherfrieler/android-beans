package rocks.frieler.android.beans.scopes.singleton

import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [singleton][SingletonScopedFactoryBeanHandler.name]-scope.
 *
 *
 * There will be only a single instance of that bean, just as it would when defined without any
 * factory except that the bean will be instantiated lazily by the factory.
 */
class SingletonScopedFactoryBean<T : Any>
private constructor(type: KClass<T>, producer: () -> T) : GenericScopedFactoryBean<T>(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE, type, producer) {

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
        fun <T : Any> lazy(type: Class<T>, producer: () -> T): SingletonScopedFactoryBean<T> {
            return SingletonScopedFactoryBean(type.kotlin, producer)
        }
    }
}
