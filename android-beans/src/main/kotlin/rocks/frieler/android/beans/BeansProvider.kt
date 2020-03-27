package rocks.frieler.android.beans

import kotlin.reflect.KClass

/**
 * Interface for classes that provide the beans of an application.
 *
 *
 * Beans are simple Java objects, that get registered with a name and can be looked up either by their name or type to
 * inject them as dependencies (DI).
 *
 * @author Christopher Frieler
 */
interface BeansProvider {
    /**
     * Looks up the bean with the given name and type.
     *
     *
     * If no bean with that name is registered or the registered bean is not assignable to the required type,
     * `null` is returned.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or `null`
     */
    fun <T :Any> lookUpBean(name: String, type: KClass<T>): T?

    /**
     * Looks up a bean of the given type.
     *
     *
     * If no bean is assignable to the required type, `null` is returned. If multiple beans are assignable to the
     * required type, the first match is returned.
     *
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return a bean of the given type or `null`
     */
    fun <T :Any> lookUpBean(type: KClass<T>): T?

    /**
     * Looks up all beans of the given type in the [BeanRegistry] of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     */
    fun <T :Any> lookUpBeans(type: KClass<T>): List<T>
}
