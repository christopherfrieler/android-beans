package rocks.frieler.android.beans.scopes

import rocks.frieler.android.beans.BeansProvider

/**
 * Interface for classes handling [ScopedFactoryBean]s of the corresponding scope.
 */
interface ScopedFactoryBeanHandler {

    /**
     * Returns the name of the scope.
     *
     * @return the name of the scope
     */
    val name: String

    /**
     * Checks whether the scope is active, i.e. if beans of this scope are currently available.
     *
     * @return `true` or `false` whether the scope is active
     */
    val isActive: Boolean

    /**
     * Returns the bean defined by the given [ScopedFactoryBean].
     *
     *
     * The bean can be an existing one already lining in this scope, if so. Otherwise a new bean is created by the
     * [factoryBean][ScopedFactoryBean].
     *
     *
     * Note: This method may only be called, when the scope [is active][.isActive].
     *
     * @param name the name of the bean, which is equal to the name of the [factory bean][ScopedFactoryBean]
     * @param factoryBean the [factory bean][ScopedFactoryBean] to produce the desired bean
     * @param dependencies a [BeansProvider] to obtain dependencies
     * @param <T> the type of the bean
     * @return the existing bean in this scope or a new one
    </T> */
    fun <T :Any> getBean(name: String, factoryBean: ScopedFactoryBean<T>, dependencies: BeansProvider): T
}
