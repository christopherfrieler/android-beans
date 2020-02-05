package rocks.frieler.android.beans

import java8.util.Optional
import rocks.frieler.android.beans.BeanDependency.Fulfillment
import java.util.*

/**
 * Abstract super-class to define beans for the context of an application.
 *
 *
 * [BeanConfiguration]s must provide a public constructor, which takes the [android.content.Context]
 * or no arguments, to be instantiated.
 *
 *
 * Usage example:
 * ```java
 * // 1. extend BeanConfiguration:
 * public class MyBeanConfiguration extends BeanConfiguration {
 *
 * 	// 2. declare dependencies to other beans using the require-methods:
 * 	private final BeanDependency&lt;AnotherBean&gt; dependency = requireBean(AnotherBean.class);
 *
 * 	// 3. define beans:
 * 	public void defineBeans(BeansCollector beansCollector) {
 * 		beansCollector.defineBean(new MyBean(dependency.get()));
 * 	}
 * }
 *```
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
	 * Creates and registers a [BeanDependency] on a bean with the given name and of the given type.
	 *
	 * @param name the name of the desired bean
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBean(name: String, type: Class<T>): BeanDependency<T> =
			addDependency(SingleBeanDependency(name, type))

	/**
	 * Creates and registers a [BeanDependency] on a bean of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBean(type: Class<T>): BeanDependency<T> =
			addDependency(SingleBeanDependency(type))

	/**
	 * Creates and registers a [BeanDependency] on a bean of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T: Any> requireOptionalBean(type: Class<T>): BeanDependency<Optional<T>> =
			addDependency(OptionalSingleBeanDependency(type))

	/**
	 * Creates and registers a [BeanDependency] on the beans of the given type.
	 *
	 * @param type the type of the desired bean
	 * @param <T> the type of the desired bean
	 * @return the [BeanDependency] on the desired bean
	 */
	fun <T :Any> requireBeans(type: Class<T>): BeanDependency<List<T>> =
			addDependency(BeansOfTypeDependency(type))

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
	 * Defines beans for the context of this application by calling
	 * [BeanConfigurationsBeansCollector.defineBean].
	 *
	 *
	 * Other beans can by obtained by declaring [BeanDependency]s to satisfy dependencies of the defined beans
	 * through [dependencies]. Hence, the require-methods that register [BeanDependency]s must not be
	 * called inside this method; this must be done earlier. And take care not to create cyclic dependencies between
	 * [BeanConfiguration]s which are unresolvable.
	 *
	 *
	 * This method must not be called before [.isReadyToDefineBeans] returned
	 * `true`.
	 *
	 * @param beansCollector the [BeanConfigurationsBeansCollector] that collects the beans
	 *
	 * @see BeanConfigurationsBeansCollector.defineBean
	 */
	abstract fun defineBeans(beansCollector: BeansCollector)
}
