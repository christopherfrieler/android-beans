package rocks.frieler.android.beans

import rocks.frieler.android.beans.BeanDependency.Fulfillment
import java.util.Objects
import kotlin.reflect.KClass

/**
 * [BeanDependency] to express a dependency on the beans of a certain type.
 *
 *
 * Be aware that this dependency cannot guarantee to provide *all* beans of that type, because the exact runtime
 * type of a bean and whether it is a subtype of the desired type cannot be determined before the bean's construction.
 * Hence this dependency always indicates to be [unfulfilled, but optional][Fulfillment.UNFULFILLED_OPTIONAL]. It
 * will gather as many beans as possible by obtaining the beans as late as possible.
 *
 * @param <T> the bean-type
 */
class BeansOfTypeDependency<T :Any>(
		private val type: KClass<T>
) : BeanDependency<List<T>> {

	private lateinit var beansProvider: BeansProvider
	/**
	 * Always returns [Fulfillment.UNFULFILLED_OPTIONAL], because it cannot be known if there will be any more
	 * beans of the desired type defined in the future. The given [BeansProvider] will be used to obtain the beans
	 * lazily when needed.
	 */
	override fun fulfill(beansProvider: BeansProvider): Fulfillment {
		this.beansProvider = beansProvider
		return Fulfillment.UNFULFILLED_OPTIONAL
	}

	override fun equals(other: Any?): Boolean {
		return if (javaClass == other?.javaClass) {
			other as BeansOfTypeDependency<*>
			Objects.equals(type, other.type)
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		return Objects.hash(type)
	}
}
