package rocks.frieler.android.beans

import java8.util.Optional
import rocks.frieler.android.beans.BeanDependency.Fulfillment
import java.util.Objects
import kotlin.reflect.KClass

/**
 * [BeanDependency] to express an optional dependency on a single bean.
 *
 *
 * This kind of dependency always indicates to be [fulfilled][.fulfill] and will try to provide the
 * wanted by obtaining it from the [BeansProvider] as late as possible.
 *
 * @param <T> the bean-type
 */
class OptionalSingleBeanDependency<T :Any>(
		private val name: String?,
		private val type: KClass<out T>
) : BeanDependency<Optional<T>> {

	private var bean: T? = null

	/**
	 * Creates a new optional [BeanDependency] to a bean of the given type.
	 *
	 * @param type the type of the bean
	 */
	constructor(type: KClass<out T>) : this(null, type) {}

	/**
	 * Tries to fulfill this [BeanDependency] with beans from the given [BeansProvider] and returns
	 * the [state of fulfillment][Fulfillment].
	 *
	 * As long as there is no suitable bean for this dependency is available, this dependency is
	 * [Fulfillment.UNFULFILLED_OPTIONAL]. After a suitable bean could be obtained, this dependency is
	 * [Fulfillment.FULFILLED] and the [BeansProvider] will no linger be queried.
	 *
	 * @param beansProvider the [BeansProvider] to obtain beans from
	 * @return the [Fulfillment]
	 */
	override fun fulfill(beansProvider: BeansProvider): Fulfillment {
		if (bean == null) {
			bean = if (name != null) beansProvider.lookUpBean(name, type) else beansProvider.lookUpBean(type)
		}
		return if (bean != null) Fulfillment.FULFILLED else Fulfillment.UNFULFILLED_OPTIONAL
	}

	override fun get(): Optional<T> {
		return Optional.ofNullable(bean)
	}

	override fun equals(other: Any?): Boolean {
		return if (javaClass == other?.javaClass) {
			other as OptionalSingleBeanDependency<*>
			Objects.equals(name, other.name) && Objects.equals(type, other.type)
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		return Objects.hash(name, type)
	}
}
