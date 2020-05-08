package rocks.frieler.android.beans

import androidx.annotation.VisibleForTesting
import kotlin.reflect.KClass

/**
 * Abstract super-class for [BeanConfiguration]s to define their beans in a declarative fashion.
 *
 *
 * Usage example:
 * ```kotlin
 * // 1. extend BeanConfiguration:
 * class MyBeanConfiguration : DeclarativeBeanConfiguration() {
 *
 *   // 2. declare dependencies to other beans using the require-methods:
 *   private val anotherBean = requireBean(type = AnotherBean::class)
 *
 *   // 3. define beans:
 *   override fun beans() {
 *     bean("my_bean") {
 *       MyBean(anotherBean.get())
 *     }
 *   }
 * }
 * ```
 */
abstract class DeclarativeBeanConfiguration : BeanConfiguration() {
	private val beanDefinitions: MutableList<BeanDefinition<*>> = ArrayList()

	private var hasDeclaredBeans = false

	final override fun getBeanDefinitions(): List<BeanDefinition<*>> {
		if (!hasDeclaredBeans) {
			hasDeclaredBeans = true
			beans()
		}

		return beanDefinitions
	}

	abstract fun beans()

	/**
	 * Defines a bean, optionally with the specified name.
	 *
	 * @param name the bean's name (optional)
	 * @param definition the definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	inline fun <reified T : Any> bean(name: String? = null, noinline definition: BeansProvider.() -> T): BeanReference<T> {
		return addBeanDefinition(name, T::class, definition)
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
		return addBeanDefinition(name, type.kotlin, definition)
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
		return addBeanDefinition(name, type.kotlin) { definition() }
	}

	/**
	 * Defines a bean (optionally with the specified name) of a Java type.
	 *
	 *
	 * This version of `bean()` is intended for Java interoperability. It allows to invoke `bean()`
	 * with the result of a static convenience function, e.g. for scopes:
	 * ```
	 * bean("myScopedBean",
	 *     ABeanScope.scoped(MyScopedBean.class, MyScopedBean::new)
	 * );
	 * ```
	 *
	 * @param name the bean's name (optional)
	 * @param typeAndDefinition the bean's type and definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	@JvmOverloads
	fun <T : Any> bean(name: String? = null, typeAndDefinition: Pair<Class<T>, BeansProvider.() -> T>): BeanReference<T> {
		return bean(name, typeAndDefinition.first, typeAndDefinition.second)
	}

	@VisibleForTesting
	fun <T : Any> addBeanDefinition(name: String?, type: KClass<T>, definition: (BeansProvider) -> T): BeanDefinition<T> {
		val beanDefinition = BeanDefinition(name, type, definition)
		beanDefinitions.add(beanDefinition)
		return beanDefinition
	}
}
