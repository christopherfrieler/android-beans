package rocks.frieler.android.beans.scopes

import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.kotlin.reflect.isAssignableFrom
import kotlin.reflect.KClass

class ScopedBeanDefinition<F : ScopedFactoryBean<*>, T : Any>(
		name: String? = null,
		factoryBeanType: KClass<F>,
		private val targetType: KClass<T>,
		creator: (BeansProvider) -> F
) : BeanDefinition<F>(name, factoryBeanType, creator) {

	override fun canProduce(type: KClass<*>): Boolean {
		return type.isAssignableFrom(targetType) || super.canProduce(type)
	}
}
