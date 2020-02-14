package rocks.frieler.android.beans

/**
 * [RuntimeException] thrown, when beans could not be instantiated.
 *
 * @author Christopher Frieler
 */
class BeanInstantiationException : RuntimeException {
	constructor(message: String?) : super(message)
	constructor(message: String?, cause: Throwable?) : super(message, cause)
}
