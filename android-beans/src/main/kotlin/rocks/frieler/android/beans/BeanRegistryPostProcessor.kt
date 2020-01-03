package rocks.frieler.android.beans

/**
 * Callback-interface that allows to post-process a [BeanRegistry] after all beans are collected.
 *
 *
 * Implementers of this interface that are registered in a [BeanRegistry] are applied to it automatically.
 *
 * @author Christopher Frieler
 */
interface BeanRegistryPostProcessor {
    fun postProcess(beanRegistry: BeanRegistry)
}
