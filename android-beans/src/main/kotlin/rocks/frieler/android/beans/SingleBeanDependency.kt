package rocks.frieler.android.beans

import rocks.frieler.android.beans.BeanDependency.Fulfillment

/**
 * [BeanDependency] to express a dependency on a single bean.
 *
 * @param <T> the bean-type
 */
class SingleBeanDependency<T :Any>(
		private val name: String?,
		private val type: Class<out T>
) : BeanDependency<T> {

	constructor(type: Class<out T>) : this(null, type)

	private var bean: T? = null

	override fun fulfill(beansProvider: BeansProvider): Fulfillment {
		if (bean == null) {
			bean = if (name == null) beansProvider.lookUpBean(type) else beansProvider.lookUpBean(name, type)
		}
		return if (bean != null) Fulfillment.FULFILLED else Fulfillment.UNFULFILLED
	}

	override fun get(): T? {
		return bean
	}
}
