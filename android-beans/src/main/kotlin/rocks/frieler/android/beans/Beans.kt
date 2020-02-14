package rocks.frieler.android.beans

import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler

/**
 * Facade-object to access the beans in the global [BeansProvider].
 *
 * [Beans] needs to be initialized with the [Beans.Initializer] first.
 */
object Beans {
    private lateinit var beansProvider: BeansProvider

    internal fun setBeans(beansProvider: BeansProvider) {
        Beans.beansProvider = beansProvider
    }

    /**
     * Looks up the bean with the given name and type in the [BeansProvider] of this application.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or `null`
     *
     * @see BeansProvider.lookUpBean
     */
    @JvmStatic
    fun <T :Any> lookUpBean(name: String, type: Class<T>): T? {
        return beansProvider.lookUpBean(name, type)
    }

    /**
     * Looks up a bean of the given type in the [BeansProvider] of this application.
     *
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return a bean of the given type or `null`
     *
     * @see BeansProvider.lookUpBean
     */
    @JvmStatic
    fun <T :Any> lookUpBean(type: Class<T>): T? {
        return beansProvider.lookUpBean(type)
    }

    /**
     * Looks up all beans of the given type in the [BeansProvider] of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     *
     * @see BeansProvider.lookUpBeans
     */
    @JvmStatic
    fun <T :Any> lookUpBeans(type: Class<T>): List<T> {
        return beansProvider.lookUpBeans(type)
    }

    /**
     * Initializer for [Beans].
     *
     * Initializes [Beans] with a [BeanRegistry] and allows to configure it.
     */
    class Initializer {
        private val beanRegistry = BeanRegistry()

        /**
         * Adds a bean-scope expressed by the given [ScopedFactoryBeanHandler].
         *
         * @param scopedFactoryBeanHandler the [ScopedFactoryBeanHandler] to add
         * @return the [Initializer] itself
         *
         * @see BeanRegistry.addBeanScope
         */
        fun addScope(scopedFactoryBeanHandler: ScopedFactoryBeanHandler): Initializer {
            beanRegistry.addBeanScope(scopedFactoryBeanHandler)
            return this
        }

        /**
         * Collects beans from the given [BeanConfiguration]s.
         *
         * @param beanConfigurations the BeanConfigurations that define the beans
         * @return the [Initializer] itself
         *
         * @see BeanConfigurationsBeansCollector.collectBeans
         */
        fun collectBeans(beanConfigurations: List<BeanConfiguration>): Initializer {
            BeanConfigurationsBeansCollector(beanRegistry).collectBeans(beanConfigurations)
            return this
        }

        /**
         * Initializes [Beans] with the applied configuration.
         */
        fun initialize() {
            setBeans(beanRegistry)
        }
    }
}
