package rocks.frieler.android.beans

import rocks.frieler.kotlin.reflect.isAssignableFrom
import kotlin.reflect.KClass

open class BeanDefinition<T : Any>(
		private val name: String? = null,
		private val type: KClass<T>,
		private val creator: (BeansProvider) -> T
) {

	fun getName() : String? {
		return name
	}

	fun getType() : KClass<T> {
		return type
	}

	open fun canProduce(type: KClass<*>) : Boolean {
		return type.isAssignableFrom(this.type)
	}

	fun produceBean(dependencyProvider: BeansProvider) : T {
		return creator.invoke(dependencyProvider)
	}
}
