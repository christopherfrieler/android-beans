package rocks.frieler.android.beans

import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler
import kotlin.reflect.KClass

/**
 * Facade-object to access the beans in the global [BeansProvider].
 *
 * [Beans] needs to be initialized with the [Beans.Initializer] first.
 *
 * @author Christopher Frieler
 */
object Beans {
    private lateinit var beansProvider: BeansProvider

    internal fun setBeans(beansProvider: BeansProvider) {
        Beans.beansProvider = beansProvider
    }

    /**
     * Looks up the bean with the inferred type and given name (if not `null`) in the
     * [BeansProvider] of this application.
     *
     * @param name the name of the desired bean (optional)
     * @param <T> the desired bean-type
     * @return the bean
     *
     * @see lookUpBean
     */
    inline fun <reified T : Any> lookUpBean(name: String? = null) = lookUpBean(name, T::class)

    /**
     * Looks up the bean with the given name (if not `null`) and type in the [BeansProvider] of this
     * application.
     *
     * @param name the name of the desired bean (optional)
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the bean
     *
     * @see BeansProvider.lookUpBean
     */
    fun <T : Any> lookUpBean(name: String? = null, type: KClass<T>): T {
        return if (name == null)
            beansProvider.lookUpBean(type)
        else
            beansProvider.lookUpBean(name, type)
    }

    /**
     * Looks up the bean with the given name (if not `null`) and type in the [BeansProvider] of this
     * application.
     *
     * @param name the name of the desired bean (optional)
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean
     *
     * @see lookUpBean
     */
    @JvmStatic
    @JvmOverloads
    fun <T :Any> lookUpBean(name: String? = null, type: Class<T>) = lookUpBean(name, type.kotlin)

    /**
     * Looks up the bean with the inferred type and given name (if not `null`) in the
     * [BeansProvider] of this application.
     *
     * @param name the name of the desired bean (optional)
     * @param <T> the desired bean-type
     * @return the bean or `null`
     *
     * @see lookUpOptionalBean
     */
    inline fun <reified T : Any> lookUpOptionalBean(name: String? = null) = lookUpOptionalBean(name, T::class)

    /**
     * Looks up the bean with the given name (if not `null`) and type in the [BeansProvider] of this
     * application.
     *
     * @param name the name of the desired bean (optional)
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the bean or `null`
     *
     * @see BeansProvider.lookUpOptionalBean
     */
    fun <T : Any> lookUpOptionalBean(name: String? = null, type: KClass<T>): T? {
        return if (name == null)
            beansProvider.lookUpOptionalBean(type)
        else
            beansProvider.lookUpOptionalBean(name, type)
    }

    /**
     * Looks up the bean with the given name (if not `null`) and type in the [BeansProvider] of this
     * application.
     *
     * @param name the name of the desired bean (optional)
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or `null`
     *
     * @see lookUpOptionalBean
     */
    @JvmStatic
    @JvmOverloads
    fun <T :Any> lookUpOptionalBean(name: String? = null, type: Class<T>) = lookUpOptionalBean(name, type.kotlin)

    /**
     * Looks up all beans of the inferred type in the [BeansProvider] of this application.
     *
     * @param <T> the desired bean-type
     * @return the beans of the given type or an empty list
     *
     * @see BeansProvider.lookUpBeans
     */
    inline fun <reified T : Any> lookUpBeans() = lookUpBeans(T::class)

    /**
     * Looks up all beans of the given type in the [BeansProvider] of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     *
     * @see BeansProvider.lookUpBeans
     */
    fun <T : Any> lookUpBeans(type: KClass<T>): List<T> {
        return beansProvider.lookUpBeans(type)
    }

    /**
     * Looks up all beans of the given type in the [BeansProvider] of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     *
     * @see lookUpBeans
     */
    @JvmStatic
    fun <T :Any> lookUpBeans(type: Class<T>) = lookUpBeans(type.kotlin)

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
