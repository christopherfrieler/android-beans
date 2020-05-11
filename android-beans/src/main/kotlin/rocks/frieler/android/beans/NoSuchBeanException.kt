package rocks.frieler.android.beans

import kotlin.reflect.KClass

/**
 * [RuntimeException] thrown, when a desired bean is not available.
 *
 * @author Christopher Frieler
 */
class NoSuchBeanException(
		val name : String?,
		val type : KClass<*>
) : RuntimeException("No bean of type '${type.qualifiedName}'"
		+ (if (name != null) " named '$name'" else "")
		+ " found.") {

	constructor(type: KClass<*>) : this(null, type)
}
