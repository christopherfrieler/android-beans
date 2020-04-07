package rocks.frieler.android.beans.scopes.singleton

import rocks.frieler.android.beans.DeclarativeBeanConfiguration
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
class SingletonScopedFactoryBean<T : Any>(type: KClass<T>, producer: () -> T)
	: GenericScopedFactoryBean<T>(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE, type, producer) {

    companion object {
        /**
         * Provides type and definition for a [SingletonScopedFactoryBean] to produce a bean of the
		 * given type using the given producer.
         *
         *
         * Since beans are singletons by default, this has the effect of a lazy instantiation.
         *
         * @param type the type of bean produced
         * @param producer the producer to lazily create the actual bean
         * @param <T> the type of bean produced
         * @return type and definition for a [SingletonScopedFactoryBean]
         */
		@JvmStatic
		fun <T : Any> lazyInstantiated(type: Class<T>, producer: () -> T): Pair<Class<SingletonScopedFactoryBean<*>>, () -> SingletonScopedFactoryBean<T>> {
			return Pair(SingletonScopedFactoryBean::class.java, { SingletonScopedFactoryBean(type.kotlin, producer) })
		}
	}
}

inline fun <reified T : Any> DeclarativeBeanConfiguration.lazyInstantiatedBean(name: String? = null, noinline definition: () -> T) {
	bean(name) {
		SingletonScopedFactoryBean(T::class, definition)
	}
}
