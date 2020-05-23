package rocks.frieler.android.beans

import rocks.frieler.kotlin.reflect.isAssignableFrom
import kotlin.reflect.KClass

/**
 * A [BeanDefinition] defines a bean through its type, a function to create the bean and optionally
 * with a name.
 *
 * @author Christopher Frieler
 */
open class BeanDefinition<T : Any>(
		private val name: String? = null,
		private val type: KClass<T>,
		private val creator: (BeansProvider) -> T
) {

	/**
	 * Returns the name for the defined bean or `null` if no name was specified explicitly.
	 *
	 * @return the specified name for the bean
	 */
	fun getName() : String? {
		return name
	}

	/**
	 * Returns the [type](KClass) of the defined bean.
	 *
	 * Note: This is a specified or inferred upper boundary for the actual type of the bean, which
	 * might also be of a subtype.
	 *
	 * @return the [type](KClass) of the defined bean
	 */
	fun getType() : KClass<T> {
		return type
	}

	/**
	 * Indicates whether this [BeanDefinition] will produce a bean of the given [type](KCLass) (or a
	 * subtype).
	 *
	 * @param type the type to compare with the bean's type
	 * @return `true`, if the desired type is assignable from the [bean's type](getType), `false`
	 * otherwise
	 */
	open fun canProduce(type: KClass<*>) : Boolean {
		return type.isAssignableFrom(this.type)
	}

	/**
	 * Produces the bean by invoking the creator function.
	 *
	 * @param dependencyProvider a [BeansProvider] to obtain dependencies
	 * @return the bean
	 */
	fun produceBean(dependencyProvider: BeansProvider) : T {
		return creator.invoke(dependencyProvider)
	}
}
