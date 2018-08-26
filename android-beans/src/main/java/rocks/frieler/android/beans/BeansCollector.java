package rocks.frieler.android.beans;

/**
 * Interface for classes that allow to define beans.
 * <p>
 * These beans are simple Java objects, that get defined together with a name and can be looked up either by their name
 * or type later to inject them as dependencies (DI), e.g. through a {@link BeansProvider}.
 *
 * @author Christopher Frieler
 */
public interface BeansCollector {
    /**
     * Defines the given bean.
     * <p>
     * The name for the bean will be generated by the implementation.
     *
     * @param bean the bean to define
     */
    void defineBean(Object bean);

    /**
     * Defines the given bean with the specified name.
     *
     * @param name the name for the bean
     * @param bean the bean to define
     */
    void defineBean(String name, Object bean);

    /**
     * Registers the given {@link BeanPostProcessor}.
     * <p>
     * The {@link BeanPostProcessor} will get all beans already defined to post-process immediately and all beans
     * defined subsequently.
     *
     * @param beanPostProcessor the new {@link BeanPostProcessor}
     */
    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}