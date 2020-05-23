package rocks.frieler.android.beans

/**
 * Interface to post-process beans.
 *
 * Beans implementing this interface are applied automatically when registered at a [BeanRegistry].
 *
 * @author Christopher Frieler
 */
interface BeanPostProcessor {

    /**
     * Allows to post-process beans of a [BeanRegistry].
     *
     * The bean can be changed or even replaced, but must have the same type.
     *
     * @param name the bean name
     * @param bean the bean to post-process
     * @param <T> the type of the bean
     * @return the post-processed bean, either the original or a replacement
     */
    fun <T :Any> postProcessBean(name: String, bean: T): T
}
