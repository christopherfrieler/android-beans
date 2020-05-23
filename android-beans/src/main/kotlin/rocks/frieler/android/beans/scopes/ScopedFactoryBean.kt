package rocks.frieler.android.beans.scopes

import rocks.frieler.android.beans.BeansProvider
import kotlin.reflect.KClass

/**
 * Interface for factory-beans, that produce the actual bean living in a limited scope, when needed.
 *
 * @param <T> the type of bean produced
 *
 * @author Christopher Frieler
 */
interface ScopedFactoryBean<T : Any> {

    /**
     * Returns the name of the scope, where the bean produced by this [ScopedFactoryBean] lives in.
     *
     * @return the name of the scope
     */
    val scope: String

    /**
     * Returns the type of the bean produced by this [ScopedFactoryBean].
     *
     * @return the type of the bean
     */
    val beanType: KClass<T>

    /**
     * Produces a new bean of type [T].
     *
     * @param dependencies a [BeansProvider] to obtain dependencies
     * @return a new bean of type [T]
     */
    fun produceBean(dependencies: BeansProvider): T
}
