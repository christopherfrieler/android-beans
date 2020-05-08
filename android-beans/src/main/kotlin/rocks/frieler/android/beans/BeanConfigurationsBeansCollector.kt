package rocks.frieler.android.beans

import rocks.frieler.android.beans.BeanConfiguration.Readiness
import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * The [BeanConfigurationsBeansCollector] collects the beans defined by [BeanConfiguration]s in a
 * [BeanRegistry].
 *
 *
 * The [BeanConfigurationsBeansCollector] also implements the [BeansProvider]-interface by providing
 * the beans that are already registered in the [BeanRegistry] to provide them as dependencies for
 * further beans.
 *
 * @author Christopher Frieler
 */
class BeanConfigurationsBeansCollector
internal constructor(
	private val beanRegistry: BeanRegistry
) : BeansProvider {

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
				produceBeans(beanConfiguration)
				limit = remainingBeanConfigurations.size
			} else if (includingDelayed && beanConfiguration.isReadyToDefineBeans(this) === Readiness.DELAY) {
				produceBeans(beanConfiguration)
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

	private fun produceBeans(beanConfiguration: BeanConfiguration) {
		beanConfiguration.getBeanDefinitions().forEach {
			if (it.getName() != null) {
				this.beanRegistry.registerBean(it.getName()!!, it.produceBean(this))
			} else {
				this.beanRegistry.registerBean(it.produceBean(this))
			}
		}
	}

	private fun applyBeanRegistryPostProcessors() {
		for (postProcessor in beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class)) {
			postProcessor.postProcess(beanRegistry)
		}
	}

	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpBean(name: String, type: KClass<T>): T? {
		return beanRegistry.lookUpBean(name, type)
	}


	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpBean(type: KClass<T>): T? {
		return beanRegistry.lookUpBean(type)
	}


	/**
	 * Delegates to the underlying [BeanRegistry].
	 *
	 * @see BeanRegistry.lookUpBeans
	 */
	override fun <T :Any> lookUpBeans(type: KClass<T>): List<T> {
		return beanRegistry.lookUpBeans(type)
	}
}
