package rocks.frieler.android.beans

import rocks.frieler.android.beans.BeanDependency.Fulfillment
import java.util.Objects
import kotlin.reflect.KClass

/**
 * [BeanDependency] to express a dependency on a single bean.
 *
 * @param <T> the bean-type
 */
class SingleBeanDependency<T :Any>(
		private val name: String?,
		private val type: KClass<out T>
) : BeanDependency<T> {

	constructor(type: KClass<out T>) : this(null, type)

	private var bean: T? = null

	override fun fulfill(beansProvider: BeansProvider): Fulfillment {
		if (bean == null) {
			bean = if (name == null) beansProvider.lookUpBean(type) else beansProvider.lookUpBean(name, type)
		}
		return if (bean != null) Fulfillment.FULFILLED else Fulfillment.UNFULFILLED
	}

	override fun equals(other: Any?): Boolean {
		return if (javaClass == other?.javaClass) {
			other as SingleBeanDependency<*>
			Objects.equals(name, other.name) && Objects.equals(type, other.type)
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		return Objects.hash(name, type)
	}
}
