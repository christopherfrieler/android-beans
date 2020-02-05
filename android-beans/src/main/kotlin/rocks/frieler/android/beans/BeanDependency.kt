package rocks.frieler.android.beans

/**
 * Interface to express a dependency on one ore more beans to be available from a [BeansProvider].
 *
 * @param <T> the target-type of this [BeanDependency]
 *
 * @author Christopher Frieler
 */
interface BeanDependency<T :Any> {

	/**
	 * Tries to fulfill this [BeanDependency] with beans from the given [BeansProvider] and returns
	 * the [state of fulfillment][Fulfillment].
	 *
	 * @param beansProvider the [BeansProvider] to obtain beans from
	 * @return the [Fulfillment]
	 */
	fun fulfill(beansProvider: BeansProvider): Fulfillment

	/**
	 * Indicates the state of a [BeanDependency].
	 *
	 * @see .fulfill
	 */
	enum class Fulfillment {
		/**
		 * Indicates that the [BeanDependency] could not be fulfilled with the available beans.
		 */
		UNFULFILLED,
		/**
		 * Indicates that the [BeanDependency] could not be fulfilled with the available beans, but is optional.
		 * Fulfilling it should be retried when there are further beans available. But if it's never fulfilled, that's
		 * ok, too.
		 */
		UNFULFILLED_OPTIONAL,
		/**
		 * Indicates that the [BeanDependency] was fulfilled with the available beans.
		 */
		FULFILLED;

		companion object Comparison : Comparator<Fulfillment> {
			val MAXIMUM = FULFILLED

			override fun compare(aFulfillment: Fulfillment, anotherFulfillment: Fulfillment): Int {
				return aFulfillment.ordinal - anotherFulfillment.ordinal
			}
		}
	}

	/**
	 * Returns the target of this [BeanDependency] after it was fulfilled.
	 *
	 * @return the target of this [BeanDependency]
	 */
	fun get(): T?
}
