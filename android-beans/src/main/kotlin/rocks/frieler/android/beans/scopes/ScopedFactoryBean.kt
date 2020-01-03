package rocks.frieler.android.beans.scopes

import android.app.Activity

/**
 * Interface for factory-beans, that produce the actual bean living in a limited scope, when needed.
 *
 * @param <T> the type of bean produced
 */
interface ScopedFactoryBean<T> {

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
    val beanType: Class<T>

    /**
     * Produces a new bean of type [T] corresponding to the given [Activity].
     *
     * @return a new bean of type [T] corresponding to the given [Activity]
     */
    fun produceBean(): T
}
