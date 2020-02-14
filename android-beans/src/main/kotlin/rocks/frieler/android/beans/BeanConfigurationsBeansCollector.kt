package rocks.frieler.android.beans

import rocks.frieler.android.beans.BeanConfiguration.Readiness
import java.util.*

/**
 * The [BeanConfigurationsBeansCollector] collects the beans defined by [BeanConfiguration]s in a
 * [BeanRegistry].
 *
 *
 * The [BeanConfigurationsBeansCollector] also implements the [BeansProvider]-interface by providing
 * the beans that are already registered in the [BeanRegistry].
 *
 * @author Christopher Frieler
 */
class BeanConfigurationsBeansCollector
internal constructor(
	private val beanRegistry: BeanRegistry
) : BeansCollector, BeansProvider {

	private val remainingBeanConfigurations: MutableList<BeanConfiguration> = LinkedList()

	/**
	 * Collects beans from the given [BeanConfiguration]s and registers them at the underlying
	 * [BeanRegistry].
	 *
	 * @param beanConfigurations the [BeanConfiguration]s that define the beans
	 */
	fun collectBeans(beanConfigurations: List<BeanConfiguration>) {
		remainingBeanConfigurations.addAll(beanConfigurations)
		collectRemainingBeans()
		if (remainingBeanConfigurations.isNotEmpty()) {
			throw BeanInstantiationException("could not collect all beans from " +
					"BeanConfigurations. maybe there is a cyclic dependency.")
		}
		applyBeanRegistryPostProcessors()
	}

	private fun collectRemainingBeans() {
		var limit = remainingBeanConfigurations.size
		var includingDelayed = false
		while (limit > 0) {
			val beanConfiguration = remainingBeanConfigurations.removeAt(0)
			if (beanConfiguration.isReadyToDefineBeans(this) === Readiness.READY) {
				beanConfiguration.defineBeans(this)
				limit = remainingBeanConfigurations.size
			} else if (includingDelayed && beanConfiguration.isReadyToDefineBeans(this) === Readiness.DELAY) {
				beanConfiguration.defineBeans(this)
				includingDelayed = false
				limit = remainingBeanConfigurations.size
			} else {
				remainingBeanConfigurations.add(beanConfiguration)
				limit--
			}
			if (limit == 0 && !includingDelayed && !remainingBeanConfigurations.isEmpty()) {
				includingDelayed = true
				limit = remainingBeanConfigurations.size
			}
		}
	}

	private fun applyBeanRegistryPostProcessors() {
		for (postProcessor in beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class.java)) {
			postProcessor.postProcess(beanRegistry)
		}
	}

	/**
	 * Callback-method for [BeanConfiguration]s to define their beans.
	 *
	 * @param bean the bean
	 */
	override fun defineBean(bean: Any) {
		beanRegistry.registerBean(bean)
	}

	/**
	 * Callback-method for [BeanConfiguration]s to define their beans with an explicit name.
	 *
	 *
	 * Defining a bean with an explicit name allows to override a bean with the same name. Additionally defining an
	 * explicit name can speed-up the lookup of the bean if the lookup is done by name.
	 *
	 * @param name the name of the bean
	 * @param bean the bean
	 */
	override fun defineBean(name: String, bean: Any) {
		beanRegistry.registerBean(name, bean)
	}

	/**
	 * Registers the given [BeanPostProcessor] at the underlying [BeanRegistry].
	 *
	 * @param beanPostProcessor the new [BeanPostProcessor]
	 *
	 * @see BeanRegistry.registerBeanPostProcessor
	 */
	override fun registerBeanPostProcessor(beanPostProcessor: BeanPostProcessor) {
		beanRegistry.registerBeanPostProcessor(beanPostProcessor)
	}

	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpBean(name: String, type: Class<T>): T? {
		return beanRegistry.lookUpBean(name, type)
	}


	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpBean(type: Class<T>): T? {
		return beanRegistry.lookUpBean(type)
	}


	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBeans
	 */
	override fun <T :Any> lookUpBeans(type: Class<T>): List<T> {
		return beanRegistry.lookUpBeans(type)
	}
}
