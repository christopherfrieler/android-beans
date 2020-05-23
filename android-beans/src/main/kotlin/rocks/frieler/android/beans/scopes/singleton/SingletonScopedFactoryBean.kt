package rocks.frieler.android.beans.scopes.singleton

import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedBeanDefinition
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [singleton][SingletonScopedFactoryBeanHandler.name]-scope.
 *
 * There will be only a single instance of that bean, just as it would when defined without any
 * factory except that the bean will be instantiated lazily by the factory.
 *
 * @author Christopher Frieler
 */
class SingletonScopedFactoryBean<T : Any>(type: KClass<T>, producer: BeansProvider.() -> T)
	: GenericScopedFactoryBean<T>(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE, type, producer) {

    companion object {
        /**
         * Provides a [BeanDefinition] for a [SingletonScopedFactoryBean] that produces a bean of
		 * the given type using the given producer without dependencies.
         *
         * Since beans are singletons by default, this has the effect of a lazy instantiation.
         *
         * @param type the type of bean produced
         * @param producer the producer to lazily create the actual bean
         * @param <T> the type of bean produced
         * @return a [BeanDefinition] for a [SingletonScopedFactoryBean]
         */
		@JvmStatic
		fun <T : Any> lazyInstantiated(type: Class<T>, producer: () -> T): BeanDefinition<SingletonScopedFactoryBean<*>> {
			return lazyInstantiated(type, { _:BeansProvider -> producer() } as BeansProvider.() -> T)
		}

		/**
		 * Provides a [BeanDefinition] for a [SingletonScopedFactoryBean] that produces a bean of
		 * the given type using the given producer with dependencies.
		 *
		 * Since beans are singletons by default, this has the effect of a lazy instantiation.
		 *
		 * @param type the type of bean produced
		 * @param producer the producer to lazily create the actual bean
		 * @param <T> the type of bean produced
		 * @return a [BeanDefinition] for a [SingletonScopedFactoryBean]
		 */
		@JvmStatic
		fun <T : Any> lazyInstantiated(type: Class<T>, producer: BeansProvider.() -> T): BeanDefinition<SingletonScopedFactoryBean<*>> {
			return ScopedBeanDefinition(factoryBeanType = SingletonScopedFactoryBean::class, targetType = type.kotlin) { SingletonScopedFactoryBean(type.kotlin, producer) }
		}
	}
}

/**
 * Adds a [BeanDefinition] for a [SingletonScopedFactoryBean] that produces a bean with the given
 * definition, optionally with the specified name.
 *
 * Since beans are singletons by default, this has the effect of a lazy instantiation.
 *
 * @param name the bean's name (optional)
 * @param definition the definition to construct the bean
 * @param <T> the type of bean produced
 */
inline fun <reified T : Any> DeclarativeBeanConfiguration.lazyInstantiatedBean(name: String? = null, noinline definition: BeansProvider.() -> T) {
	addBeanDefinition(ScopedBeanDefinition(name, SingletonScopedFactoryBean::class, T::class) { SingletonScopedFactoryBean(T::class, definition) })
}
