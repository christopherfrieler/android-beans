package rocks.frieler.android.beans

import kotlin.reflect.KClass

/**
 * Abstract [BeansProvider] that provides beans from a parent [BeansProvider] additionally or as
 * fallback.
 */
abstract class HierarchicalBeansProvider(
		private val parent: BeansProvider? = null,
) : BeansProvider {

	/**
	 * Looks up the bean with the given name and type.
	 *
	 *
	 * The bean is first looked up locally and then, if not found, in the parent.
	 *
	 * @param name the name of the desired bean
	 * @param type the type of the desired bean
	 * @param <T> the bean-type
	 * @return the named bean or `null`
	 */
	final override fun <T : Any> lookUpOptionalBean(name: String, type: KClass<T>): T? {
		return lookUpOptionalLocalBean(name, type) ?: parent?.lookUpOptionalBean(name, type)
	}

	/**
	 * Looks up the bean with the given name and type locally, i.e. solely in this [BeansProvider],
	 * not in the parent, following the same contract as [BeansProvider.lookUpOptionalBean].
	 *
	 * @param name the name of the desired bean
	 * @param type the type of the desired bean
	 * @param <T> the bean-type
	 * @return the named bean or `null`
	 * @see lookUpOptionalBean
	 */
	abstract fun <T : Any> lookUpOptionalLocalBean(name: String, type: KClass<T>): T?

	/**
	 * Looks up the bean with the given type.
	 *
	 *
	 * The bean is first looked up locally and then, if not found, in the parent.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the bean-type
	 * @return the named bean or `null`
	 */
	final override fun <T : Any> lookUpOptionalBean(type: KClass<T>): T? {
		return lookUpOptionalLocalBean(type) ?: parent?.lookUpOptionalBean(type)
	}

	/**
	 * Looks up the bean with the given type locally, i.e. solely in this [BeansProvider], not in
	 * the parent, following the same contract as [BeansProvider.lookUpOptionalBean].
	 *
	 * @param type the type of the desired bean
	 * @param <T> the bean-type
	 * @return the named bean or `null`
	 * @see lookUpOptionalBean
	 */
	abstract fun <T : Any> lookUpOptionalLocalBean(type: KClass<T>): T?

	/**
	 * Looks up all beans of the given type locally and in the parent.
	 *
	 * @param type the type of the desired beans
	 * @param <T> the bean-type
	 * @return the beans of the given type or an empty list
	 */
	final override fun <T : Any> lookUpBeans(type: KClass<T>): List<T> {
		return lookUpLocalBeans(type) + (parent?.lookUpBeans(type) ?: emptyList())
	}

	/**
	 * Looks up all beans of the given type solely locally, i.e. not in the parent.
	 *
	 * @param type the type of the desired beans
	 * @param <T> the bean-type
	 * @return the beans of the given type or an empty list
	 */
	abstract fun <T : Any> lookUpLocalBeans(type: KClass<T>): List<T>
}
