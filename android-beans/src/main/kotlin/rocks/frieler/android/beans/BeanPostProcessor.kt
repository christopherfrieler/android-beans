package rocks.frieler.android.beans

/**
 * Interface to post-process beans.
 *
 * @see BeanRegistry.registerBeanPostProcessor
 */
interface BeanPostProcessor {

    /**
     * Allows to post-process beans of a [BeanRegistry].
     *
     *
     * The bean can be changed or even replaced, but must have the same type.
     *
     * @param name the bean name
     * @param bean the bean to post-process
     * @param <T> the type of the bean
     * @return the post-processed bean, either the original or a replacement
    </T> */
    fun <T :Any> postProcessBean(name: String, bean: T): T
}
