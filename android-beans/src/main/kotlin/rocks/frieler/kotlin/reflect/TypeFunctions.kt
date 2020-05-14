package rocks.frieler.kotlin.reflect

import kotlin.reflect.KClass

fun KClass<*>.isAssignableFrom(type : KClass<*>) = javaObjectType.isAssignableFrom(type.javaObjectType)
