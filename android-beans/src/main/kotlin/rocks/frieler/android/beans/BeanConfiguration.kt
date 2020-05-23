package rocks.frieler.android.beans

/**
 * Abstract super-class to define beans for the context of an application by providing
 * [BeanDefinition]s.
 *
 * [BeanConfiguration]s can be defined as an object or must provide a public constructor, which
 * takes the [android.content.Context] or no arguments, to be instantiated.
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
	 * Adds a [BeanDefinition] to this [BeanConfiguration].
	 *
	 * @param beanDefinition the [BeanDefinition] to add
	 */
	fun <T : Any> addBeanDefinition(beanDefinition: BeanDefinition<T>) {
		beanDefinitions.add(beanDefinition)
	}
}
