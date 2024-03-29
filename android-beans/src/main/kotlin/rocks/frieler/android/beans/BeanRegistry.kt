package rocks.frieler.android.beans

import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.decorate
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBeanHandler
import rocks.frieler.kotlin.reflect.isAssignableFrom
import java.util.LinkedList
import java.util.TreeMap
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * [BeansProvider] that holds and provides beans which were explicitly registered before.
 *
 * The [BeanRegistry] supports the
 * [singleton][SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE] and
 * [prototype][PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE] scopes by default.
 *
 * [BeanRegistry] is also a [HierarchicalBeansProvider], so it supports a parent to inherit beans
 * from.
 *
 * @author Christopher Frieler
 */
class BeanRegistry internal constructor(parent: BeansProvider? = null) : HierarchicalBeansProvider(parent) {
    private val beans: MutableMap<String, Any> = HashMap()
    private val beanPostProcessors: MutableList<BeanPostProcessor> = LinkedList()

    private val beanScopes: MutableMap<String, ScopedFactoryBeanHandler> = TreeMap()

    init {
        addBeanScope(SingletonScopedFactoryBeanHandler())
        addBeanScope(PrototypeScopedFactoryBeanHandler())
    }

    /**
     * Adds a bean-scope expressed by the given [ScopedFactoryBeanHandler] to this [BeanRegistry].
     *
     * @param scopedFactoryBeanHandler the [ScopedFactoryBeanHandler] to add
     */
    fun addBeanScope(scopedFactoryBeanHandler: ScopedFactoryBeanHandler) {
        beanScopes[scopedFactoryBeanHandler.name] = scopedFactoryBeanHandler
    }

    /**
     * Registers the bean with a generated name at this [BeanRegistry] and returns this name.
     *
     * @param bean the bean
	 * @return the generated name for the bean
     */
    fun registerBean(bean: Any): String {
		val name = generateBeanName(bean)
		registerBean(name, bean)
		return name
    }

    private fun generateBeanName(bean: Any): String {
        val beanClass: KClass<*> = if (bean is ScopedFactoryBean<*>) bean.beanType else bean::class

        var beanName = getPreferredBeanName(beanClass)
        if (beans.containsKey(beanName)) {
			var suffix = 2
			while (beans.containsKey(beanName + suffix)) {
				suffix++
			}
			beanName += suffix
        }

        return beanName
    }

    /**
     * Registers the bean with the given name at this [BeanRegistry].
     *
     *
     * The bean will be post-processed by all registered [BeanPostProcessor]s. If the bean is a
	 * [BeanPostProcessor] itself, it will receive all beans already registered to post-process
	 * immediately and all beans registered subsequently, but never the bean itself.
     *
     *
     * If a bean with this name already exists, it will be replaced.
     *
     * @param name the name of the bean
     * @param bean the bean
     */
    fun registerBean(name: String, bean: Any) {
		val effectiveBean = postProcessBean(name, bean)

		if (effectiveBean is BeanPostProcessor) {
			registerBeanPostProcessor(effectiveBean)
		}

		beans[name] = effectiveBean
    }

    private fun registerBeanPostProcessor(beanPostProcessor: BeanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor)

        for (beanEntry in beans.entries) {
            beanEntry.setValue(beanPostProcessor.postProcessBean(beanEntry.key, beanEntry.value))
        }
    }

    private fun <T :Any> postProcessBean(name: String, bean: T): T {
        var postProcessedBean = bean
        for (postProcessor in beanPostProcessors) {
            postProcessedBean = postProcessor.postProcessBean(name, postProcessedBean)
        }
        return postProcessedBean
    }

	override fun <T : Any> lookUpOptionalLocalBean(name: String, type: KClass<T>): T? {
        val beanCandidate = beans[name]
        return if (beanCandidate == null) {
            null
        } else {
            resolveBeanFromCandidate(name, type, beanCandidate)
        }
	}

    override fun <T :Any> lookUpOptionalLocalBean(type: KClass<T>): T? {
        val preferredBeanName = getPreferredBeanName(type)
        val beanCandidateByPreferredName = beans[preferredBeanName]
        if (beanCandidateByPreferredName != null) {
            val bean = resolveBeanFromCandidate(preferredBeanName, type, beanCandidateByPreferredName)
            if (bean != null) {
                return bean
            }
        }

        for ((key, value) in beans) {
            val bean = resolveBeanFromCandidate(key, type, value)
            if (bean != null) {
                return bean
            }
        }

        return null
    }

    override fun <T :Any> lookUpLocalBeans(type: KClass<T>): List<T> {
        val matchingBeans: MutableList<T> = ArrayList()
        for ((key, value) in beans) {
            val bean = resolveBeanFromCandidate(key, type, value)
            if (bean != null) {
                matchingBeans.add(bean)
            }
        }
        return matchingBeans
    }

	private fun <T :Any> resolveBeanFromCandidate(name: String, type: KClass<T>, beanCandidate: Any): T? {
		if (beanCandidate is ScopedFactoryBean<*> && type.isAssignableFrom(beanCandidate.beanType)) {
			@Suppress("UNCHECKED_CAST") val factoryBean = beanCandidate as ScopedFactoryBean<T>
            val scopedFactoryBeanHandler = beanScopes[factoryBean.scope]
            if (scopedFactoryBeanHandler != null && scopedFactoryBeanHandler.isActive) {
                return scopedFactoryBeanHandler.getBean(name, factoryBean.decorate().withPostProcessing { bean: T -> postProcessBean(name, bean) }, this)
            }
        }

		@Suppress("UNCHECKED_CAST")
		return if (type.isInstance(beanCandidate)) beanCandidate as T else null
	}

    private companion object BeanNaming {
		fun getPreferredBeanName(beanClass: KClass<*>): String {
			return beanClass.jvmName
        }
    }
}
