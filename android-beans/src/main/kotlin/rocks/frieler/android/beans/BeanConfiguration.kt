package rocks.frieler.android.beans

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
	/**
	 * Returns [BeanDefinition]s for the beans defined by this [BeanConfiguration].
	 *
	 * @return a list of [BeanDefinition]s
	 */
	abstract fun getBeanDefinitions() : List<BeanDefinition<*>>
}
