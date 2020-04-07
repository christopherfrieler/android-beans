package rocks.frieler.android.beans.scopes.activity

import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.Companion.ACTIVITY_SCOPE
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE]-scope.
 */
class ActivityScopedFactoryBean<T : Any>(type: KClass<T>, producer: () -> T)
	: GenericScopedFactoryBean<T>(ACTIVITY_SCOPE, type, producer) {

	companion object {
		/**
		 * Provides type and definition for a [ActivityScopedFactoryBean] to produce a bean of the
		 * given type using the given producer.
		 *
		 * @param type the type of bean produced
		 * @param producer the producer to create new beans
		 * @param <T> the type of bean produced
		 * @return a new [ActivityScopedFactoryBean]
		 */
        @JvmStatic
        fun <T : Any> activityScoped(type: Class<T>, producer: () -> T): Pair<Class<ActivityScopedFactoryBean<*>>, () -> ActivityScopedFactoryBean<T>> {
			return Pair(ActivityScopedFactoryBean::class.java, { ActivityScopedFactoryBean(type.kotlin, producer) })
		}
	}
}

inline fun <reified T : Any> DeclarativeBeanConfiguration.activityScopedBean(name: String? = null, noinline definition: () -> T) {
	bean(name) {
		ActivityScopedFactoryBean(T::class, definition)
	}
}
