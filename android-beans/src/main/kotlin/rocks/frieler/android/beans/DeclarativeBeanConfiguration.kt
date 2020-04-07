package rocks.frieler.android.beans

import androidx.annotation.VisibleForTesting
import kotlin.reflect.KClass

abstract class DeclarativeBeanConfiguration : BeanConfiguration() {
	private val beanDefinitions: MutableList<BeanDefinition<*>> = ArrayList()

	final override fun defineBeans(beansCollector: BeansCollector) {
		beans()
		beanDefinitions.forEach {
			if (it.getName() != null) {
				beansCollector.defineBean(it.getName()!!, it.produceBean())
			} else {
				beansCollector.defineBean(it.produceBean())
			}
		}
	}

	abstract fun beans()

	/**
	 * Defines a bean, optionally with the specified name.
	 *
	 * @param name the bean's name (optional)
	 * @param definition the definition to construct the bean
	 * @return a [BeanReference] for the defined bean that will be available later
	 */
	inline fun <reified T : Any> bean(name: String? = null, noinline definition: () -> T): BeanReference<T> {
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
	fun <T : Any> bean(name: String? = null, type: Class<T>, definition: () -> T): BeanReference<T> {
		return addBeanDefinition(name, type.kotlin, definition)
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
	fun <T : Any> bean(name: String? = null, typeAndDefinition: Pair<Class<T>, () -> T>): BeanReference<T> {
		return bean(name, typeAndDefinition.first, typeAndDefinition.second)
	}

	@VisibleForTesting
	fun <T : Any> addBeanDefinition(name: String?, type: KClass<T>, definition: () -> T): BeanDefinition<T> {
		val beanDefinition = BeanDefinition(name, type, definition)
		beanDefinitions.add(beanDefinition)
		return beanDefinition
	}
}
