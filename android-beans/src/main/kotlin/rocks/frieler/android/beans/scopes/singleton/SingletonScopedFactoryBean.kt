package rocks.frieler.android.beans.scopes.singleton

import rocks.frieler.android.beans.BeansProvider
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
class SingletonScopedFactoryBean<T : Any>(type: KClass<T>, producer: BeansProvider.() -> T)
	: GenericScopedFactoryBean<T>(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE, type, producer) {

    companion object {
        /**
         * Provides type and definition for a [SingletonScopedFactoryBean] that produces a bean of
		 * the given type using the given producer without dependencies.
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
		fun <T : Any> lazyInstantiated(type: Class<T>, producer: () -> T): Pair<Class<SingletonScopedFactoryBean<*>>, BeansProvider.() -> SingletonScopedFactoryBean<T>> {
			return lazyInstantiated(type, { _:BeansProvider -> producer() } as BeansProvider.() -> T)
		}

		/**
		 * Provides type and definition for a [SingletonScopedFactoryBean] that produces a bean of
		 * the given type using the given producer with dependencies.
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
		fun <T : Any> lazyInstantiated(type: Class<T>, producer: BeansProvider.() -> T): Pair<Class<SingletonScopedFactoryBean<*>>, BeansProvider.() -> SingletonScopedFactoryBean<T>> {
			return Pair(SingletonScopedFactoryBean::class.java, { _:BeansProvider -> SingletonScopedFactoryBean(type.kotlin, producer) })
		}
	}
}

inline fun <reified T : Any> DeclarativeBeanConfiguration.lazyInstantiatedBean(name: String? = null, noinline definition: BeansProvider.() -> T) {
	bean(name) {
		SingletonScopedFactoryBean(T::class, definition)
	}
}
