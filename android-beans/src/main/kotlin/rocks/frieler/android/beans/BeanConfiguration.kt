package rocks.frieler.android.beans

import java8.util.Optional
import rocks.frieler.android.beans.BeanDependency.Fulfillment
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

	private val beanDependencies: MutableList<BeanDependency<*>> = ArrayList()

	/**
	 * Allows to express [BeanDependency]s that must be fulfilled for this [BeanConfiguration] to define its
	 * beans.
	 *
	 *
	 * By default this method returns all [BeanDependency]s registered by the require-methods such as
	 * [requireBean]
	 *
	 * @return the [BeanDependency]s of this [BeanConfiguration]
	 */
	val dependencies: List<BeanDependency<*>>
		get() = beanDependencies

	protected fun <T :Any> addDependency(dependency: BeanDependency<T>): BeanDependency<T> {
		beanDependencies.add(dependency)
		return dependency
	}

	/**
	 * Creates and registers a [BeanDependency] on a bean.
	 *
	 * @param name the name of the desired bean (optional)
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBean(name: String? = null, type: KClass<T>): BeanDependency<T> =
			addDependency(SingleBeanDependency(name, type))

	/**
	 * Creates and registers a [BeanDependency] on a bean with the given name and of the given type.
	 *
	 * @param name the name of the desired bean
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBean(name: String, type: Class<T>): BeanDependency<T> =
			requireBean(name, type.kotlin)

	/**
	 * Creates and registers a [BeanDependency] on a bean of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBean(type: Class<T>): BeanDependency<T> =
			requireBean(type = type.kotlin)

	/**
	 * Creates and registers a [BeanDependency] on a bean of the given type.
	 *
	 * @param name the name of the desired bean (optional)
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T: Any> requireOptionalBean(name: String? = null, type: KClass<T>): BeanDependency<Optional<T>> =
			addDependency(OptionalSingleBeanDependency(name, type))

	/**
	 * Creates and registers a [BeanDependency] on a bean of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T: Any> requireOptionalBean(type: Class<T>): BeanDependency<Optional<T>> =
			requireOptionalBean(type = type.kotlin)

	/**
	 * Creates and registers a [BeanDependency] on the beans of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBeans(type: KClass<T>): BeanDependency<List<T>> =
			addDependency(BeansOfTypeDependency(type))

	/**
	 * Creates and registers a [BeanDependency] on the beans of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBeans(type: Class<T>): BeanDependency<List<T>> =
			requireBeans(type.kotlin)

	/**
	 * Checks, if this [BeanConfiguration] is ready to define its beans.
	 *
	 *
	 * Therefor it tries to [fulfill][BeanDependency.fulfill] all its [BeanDependency]s
	 * returned by [.getDependencies]. The [Readiness] is derived from the minimum
	 * [Fulfillment] of the [BeanDependency]s. If all are [fulfilled][Fulfillment.FULFILLED] the
	 * BeanConfiguration is [ready][Readiness.READY], if all are at least [ unfulfilled but optional][Fulfillment.UNFULFILLED_OPTIONAL] the BeanConfiguration [should be delayed][Readiness.DELAY] to wait for additional
	 * beans and if at least one [BeanDependency] is [unfulfilled][Fulfillment.UNFULFILLED] the
	 * BeanConfiguration is [unready][Readiness.UNREADY].
	 *
	 * @param beansProvider the [BeansProvider] to fulfill [BeanDependency]s
	 * @return the [Readiness] of this [BeanConfiguration] to define its beans
	 */
	fun isReadyToDefineBeans(beansProvider: BeansProvider): Readiness {
		val minFulfillment = dependencies
				.map { d -> d.fulfill(beansProvider) }
				.minWith(Fulfillment.Comparison) ?: Fulfillment.MAXIMUM

		return when (minFulfillment) {
			Fulfillment.FULFILLED -> Readiness.READY
			Fulfillment.UNFULFILLED_OPTIONAL -> Readiness.DELAY
			Fulfillment.UNFULFILLED -> Readiness.UNREADY
		}
	}

	enum class Readiness {
		READY, DELAY, UNREADY
	}

	/**
	 * Returns [BeanDefinition]s for the beans defined by this [BeanConfiguration].
	 *
	 * @return a list of [BeanDefinition]s
	 */
	abstract fun getBeanDefinitions() : List<BeanDefinition<*>>
}
