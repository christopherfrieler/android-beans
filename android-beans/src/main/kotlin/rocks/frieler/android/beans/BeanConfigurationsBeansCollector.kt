package rocks.frieler.android.beans

import java.util.LinkedList
import kotlin.reflect.KClass

/**
 * The [BeanConfigurationsBeansCollector] collects the beans defined by [BeanConfiguration]s in a
 * [BeanRegistry].
 *
 *
 * The [BeanConfigurationsBeansCollector] also implements the [BeansProvider]-interface to provide
 * dependencies to the [BeanDefinition]s being processed. Therefore it will provide beans that are
 * already registered in the [BeanRegistry] and process further [BeanDefinition]s that can fulfill
 * the dependency.
 *
 * @author Christopher Frieler
 */
class BeanConfigurationsBeansCollector
internal constructor(
	private val beanRegistry: BeanRegistry
) : BeansProvider {

	private val remainingBeanDefinitions: MutableList<BeanDefinition<*>> = LinkedList()

	/**
	 * Collects beans from the given [BeanConfiguration]s and registers them at the underlying
	 * [BeanRegistry].
	 *
	 * @param beanConfigurations the [BeanConfiguration]s that define the beans
	 */
	fun collectBeans(beanConfigurations: List<BeanConfiguration>) {
		beanConfigurations.forEach {
			remainingBeanDefinitions.addAll(it.getBeanDefinitions())
		}

		while (remainingBeanDefinitions.isNotEmpty()) {
			process(remainingBeanDefinitions.first())
		}

		applyBeanRegistryPostProcessors()
	}

	private fun <T : Any> process(beanDefinition: BeanDefinition<T>) : String {
		remainingBeanDefinitions.remove(beanDefinition)
		val name : String
		val bean = beanDefinition.produceBean(this)
		if (beanDefinition.getName() != null) {
			name = beanDefinition.getName()!!
			this.beanRegistry.registerBean(name, bean)
		} else {
			name = this.beanRegistry.registerBean(bean)
		}
		return name
	}

	private fun applyBeanRegistryPostProcessors() {
		for (postProcessor in beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class)) {
			postProcessor.postProcess(beanRegistry)
		}
	}

	/**
	 * Provides a bean by name and type from the underlying [BeanRegistry] or, if there is no such
	 * bean yet, processes further [BeanDefinition]s that can produce that bean.
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpOptionalBean(name: String, type: KClass<T>): T? {
		beanRegistry.lookUpOptionalBean(name, type)?.also { return it }

		return findBeanDefinitionFor(name, type)?.let {
			this.process(it)
			beanRegistry.lookUpOptionalBean(name, type)
		}
	}

	/**
	 * Provides a bean by type from the underlying [BeanRegistry] or, if there is no such bean yet,
	 * processes further [BeanDefinition]s that can produce that bean.
	 *
	 * @see BeanRegistry.lookUpOptionalBean
	 */
	override fun <T :Any> lookUpOptionalBean(type: KClass<T>): T? {
		beanRegistry.lookUpOptionalBean(type)?.also { return it }

		return findBeanDefinitionFor(type = type)?.let {
			val name = this.process(it)
			beanRegistry.lookUpOptionalBean(name, type)
		}
	}

	/**
	 * Provides all beans of the given type from the underlying [BeanRegistry]. Additionally, to
	 * ensure that the result contains all such beans, processes the remaining [BeanDefinition]s
	 * that will produce a bean of that type.
	 *
	 * @see BeanRegistry.lookUpBean
	 */
	override fun <T :Any> lookUpBeans(type: KClass<T>): List<T> {
		@Suppress("ControlFlowWithEmptyBody")
		while (findBeanDefinitionFor(type = type)?.also { process(it) } != null);

		return beanRegistry.lookUpBeans(type)
	}

	private fun <T : Any> findBeanDefinitionFor(name: String? = null, type: KClass<T>) : BeanDefinition<T>? {
		@Suppress("UNCHECKED_CAST")
		return remainingBeanDefinitions.find { (name == null || it.getName() == name) && it.canProduce(type) } as BeanDefinition<T>?
	}
}
