package rocks.frieler.android.beans

/**
 * Abstract super-class for [BeanConfiguration]s to define their beans in a declarative fashion.
 *
 *
 * Usage example:
 * ```kotlin
 * // 1. extend BeanConfiguration:
 * class MyBeanConfiguration : DeclarativeBeanConfiguration() {
 *
 *   override fun beans() {
 *     // 2. define your beans:
 *     bean("my_bean") {
 *       // 3. obtain dependency to another bean:
 *     	 val anotherBean = lookUpBean(AnotherBean::class)
 *       MyBean(anotherBean!!)
 *     }
 *   }
 * }
 * ```
 */
abstract class DeclarativeBeanConfiguration : BeanConfiguration() {

	private var hasDeclaredBeans = false

	final override fun getBeanDefinitions(): List<BeanDefinition<*>> {
		if (!hasDeclaredBeans) {
			hasDeclaredBeans = true
			beans()
		}

		return super.getBeanDefinitions()
	}

	abstract fun beans()

	/**
	 * Defines a bean through a [BeanDefinition].
	 *
	 *
	 * This is mainly a wrapper around [addBeanDefinition] to provide a declarative API to be used
	 * with a factory-function for the [BeanDefinition]
	 *
	 * @param beanDefinition the [BeanDefinition]
	 * @return a [BeanReference] for the defined bean that will be available later
	 * @see addBeanDefinition
	 */
	fun <T : Any> bean(beanDefinition: BeanDefinition<T>): BeanReference<T> {
		addBeanDefinition(beanDefinition)
		return beanDefinition
	}

	/**
	 * Defines a bean, optionally with the specified name.
	 *
	 * @param name the bean's name (optional)
	 * @param definition the definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	inline fun <reified T : Any> bean(name: String? = null, noinline definition: BeansProvider.() -> T): BeanReference<T> {
		val beanDefinition = BeanDefinition(name, T::class, definition)
		addBeanDefinition(beanDefinition)
		return beanDefinition
	}

	/**
	 * Defines a bean (optionally with the specified name) of a Java type.
	 *
	 *
	 * This version of `bean()` is intended for Java interoperability.
	 *
	 * @param name the bean's name (optional)
	 * @param type the bean's type
	 * @param definition the definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	@JvmOverloads
	fun <T : Any> bean(name: String? = null, type: Class<T>, definition: BeansProvider.() -> T): BeanReference<T> {
		val beanDefinition = BeanDefinition(name, type.kotlin, definition)
		addBeanDefinition(beanDefinition)
		return beanDefinition
	}

	/**
	 * Defines a bean (optionally with the specified name) of a Java type.
	 *
	 *
	 * This version of `bean()` is intended for Java interoperability.
	 *
	 * @param name the bean's name (optional)
	 * @param type the bean's type
	 * @param definition the definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	@JvmOverloads
	fun <T : Any> bean(name: String? = null, type: Class<T>, definition: () -> T): BeanReference<T> {
		val beanDefinition = BeanDefinition(name, type.kotlin) { definition() }
		addBeanDefinition(beanDefinition)
		return beanDefinition
	}
}
