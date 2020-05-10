package rocks.frieler.android.beans

import kotlin.reflect.KClass

/**
 * Abstract super-class to define beans for the context of an application.
 *
 *
 * [BeanConfiguration]s can be defined as an object or must provide a public constructor, which
 * takes the [android.content.Context] or no arguments, to be instantiated.
 *
 *
 * @author Christopher Frieler
 */
abstract class BeanConfiguration {
	private val beanDefinitions: MutableList<BeanDefinition<*>> = ArrayList()

	/**
	 * Returns [BeanDefinition]s for the beans defined by this [BeanConfiguration].
	 *
	 * @return a list of [BeanDefinition]s
	 */
	open fun getBeanDefinitions() : List<BeanDefinition<*>> = beanDefinitions

	/**
	 * Creates and adds a [BeanDefinition] from the given parts.
	 *
	 * @param name the (optional) name of the bean to define
	 * @param type the type of bean to define
	 * @param definition the function to create the actual bean instance
	 * @param T the type of bean to define
	 * @return a [BeanDefinition] from the given parts
	 */
	fun <T : Any> addBeanDefinition(name: String?, type: KClass<T>, definition: (BeansProvider) -> T): BeanDefinition<T> {
		val beanDefinition = BeanDefinition(name, type, definition)
		beanDefinitions.add(beanDefinition)
		return beanDefinition
	}
}
