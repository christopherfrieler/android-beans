package rocks.frieler.android.beans

import kotlin.reflect.KClass

class BeanDefinition<T : Any>(
		private val name: String?,
		private val type: KClass<T>,
		private val creator: (BeansProvider) -> T
) : BeanReference<T> {

	private lateinit var bean : T

	fun getName() : String? {
		return name
	}

	fun getType() : KClass<T> {
		return type
	}

	fun produceBean(dependencyProvider: BeansProvider) : T {
		if (this::bean.isInitialized) {
			throw IllegalStateException("the bean was already produced.")
		}

		return creator.invoke(dependencyProvider).also {
			bean = it
		}
	}

	override fun use() : T {
		if (!this::bean.isInitialized) {
			throw IllegalStateException("the bean was not produced yet.")
		}

		return bean
	}
}
