package rocks.frieler.android.beans.scopes.activity

import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.Companion.ACTIVITY_SCOPE
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE]-scope.
 */
class ActivityScopedFactoryBean<T : Any> private constructor(type: KClass<T>, producer: () -> T) : GenericScopedFactoryBean<T>(ACTIVITY_SCOPE, type, producer) {

	companion object {
		/**
		 * Creates a new [ActivityScopedFactoryBean] to produce a bean of the given type using the given producer.
		 *
		 * @param type the type of bean produced
		 * @param producer the producer to create new beans
		 * @param <T> the type of bean produced
		 * @return a new [ActivityScopedFactoryBean]
		 */
        @JvmStatic
        fun <T : Any> activityScoped(type: Class<T>, producer: () -> T): ActivityScopedFactoryBean<T> {
			return ActivityScopedFactoryBean(type.kotlin, producer)
		}
	}
}
