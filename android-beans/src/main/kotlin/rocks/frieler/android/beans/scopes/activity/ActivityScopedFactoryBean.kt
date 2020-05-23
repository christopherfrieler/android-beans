package rocks.frieler.android.beans.scopes.activity

import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedBeanDefinition
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.Companion.ACTIVITY_SCOPE
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE]-scope.
 *
 * @author Christopher Frieler
 */
class ActivityScopedFactoryBean<T : Any>(type: KClass<T>, producer: BeansProvider.() -> T)
	: GenericScopedFactoryBean<T>(ACTIVITY_SCOPE, type, producer) {

	companion object {
		/**
		 * Provides a [BeanDefinition] for a [ActivityScopedFactoryBean] that produces a bean of
		 * the given type using the given producer without dependencies.
		 *
		 * @param type the type of bean produced
		 * @param producer the producer to create new beans
		 * @param <T> the type of bean produced
		 * @return a [BeanDefinition] for a [ActivityScopedFactoryBean]
		 */
        @JvmStatic
        fun <T : Any> activityScoped(type: Class<T>, producer: () -> T): BeanDefinition<ActivityScopedFactoryBean<*>> {
			return activityScoped(type, { _:BeansProvider -> producer() } as BeansProvider.() -> T)
		}

		/**
		 * Provides a [BeanDefinition] for an [ActivityScopedFactoryBean] that produces a bean of
		 * the given type using the given producer with dependencies.
		 *
		 * @param type the type of bean produced
		 * @param producer the producer to create new beans
		 * @param <T> the type of bean produced
		 * @return a [BeanDefinition] for an [ActivityScopedFactoryBean]
		 */
        @JvmStatic
        fun <T : Any> activityScoped(type: Class<T>, producer: BeansProvider.() -> T): BeanDefinition<ActivityScopedFactoryBean<*>> {
			return ScopedBeanDefinition(factoryBeanType = ActivityScopedFactoryBean::class, targetType = type.kotlin) { ActivityScopedFactoryBean(type.kotlin, producer) }
		}
	}
}

/**
 * Adds a [BeanDefinition] for an [ActivityScopedFactoryBean] that produces a bean with the given
 * definition, optionally with the specified name.
 *
 * @param name the bean's name (optional)
 * @param definition the definition to construct the bean
 * @param <T> the type of bean produced
 */
inline fun <reified T : Any> DeclarativeBeanConfiguration.activityScopedBean(name: String? = null, noinline definition: BeansProvider.() -> T) {
	addBeanDefinition(ScopedBeanDefinition(name, ActivityScopedFactoryBean::class, T::class) { ActivityScopedFactoryBean(T::class, definition) })
}
