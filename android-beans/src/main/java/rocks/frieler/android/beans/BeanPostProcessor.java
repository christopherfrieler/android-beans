package rocks.frieler.android.beans;

/**
 * Interface to post-process beans.
 *
 * @see BeanRegistry#registerBeanPostProcessor(BeanPostProcessor)
 */
public interface BeanPostProcessor {

    /**
     * Allows to post-process beans of a {@link BeanRegistry}.
     * <p>
     * The bean can be changed or even replaced, but must have the same type.
     *
     * @param name the bean name
     * @param bean the bean to post-process
     * @param <T> the type of the bean
     * @return the post-processed bean, either the original or a replacement
     */
    <T> T postProcessBean(String name, T bean);
}
