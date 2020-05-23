package rocks.frieler.android.beans.scopes

import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.kotlin.reflect.isAssignableFrom
import kotlin.reflect.KClass

/**
 * Special [BeanDefinition] for [ScopedFactoryBean]s that is also aware of the type of bean produced
 * by the factory-bean.
 *
 * @author Christopher Frieler
 */
class ScopedBeanDefinition<F : ScopedFactoryBean<*>, T : Any>(
		name: String? = null,
		factoryBeanType: KClass<F>,
		private val targetType: KClass<T>,
		creator: (BeansProvider) -> F
) : BeanDefinition<F>(name, factoryBeanType, creator) {

	/**
	 * Indicates whether this [ScopedBeanDefinition] can produce a bean of the given [type](KCLass)
	 * (or a subtype), either directly or indirectly when the [ScopedFactoryBean] produces its bean.
	 *
	 * @param type the type to compare with the bean's type
	 * @return `true`, if the desired type is assignable from the either the [ScopedFactoryBean]'s
	 * type itself or the bean it produces, `false` otherwise
	 */
	override fun canProduce(type: KClass<*>): Boolean {
		return type.isAssignableFrom(targetType) || super.canProduce(type)
	}
}
