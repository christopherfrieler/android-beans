package rocks.frieler.android.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link BeanConfigurationsBeansCollector} collects the beans defined by {@link BeanConfiguration}s in a
 * {@link BeanRegistry}.
 * <p>
 * The {@link BeanConfigurationsBeansCollector} also implements the {@link BeansProvider}-interface by providing the
 * beans that are already registered in the {@link BeanRegistry}. In addition the
 * {@link BeanConfigurationsBeansCollector} will process further {@link BeanConfiguration}s to fulfill bean-lookups for
 * beans that are not present yet.
 *
 * @author Christopher Frieler
 */
public class BeanConfigurationsBeansCollector implements BeansProvider {
    private final BeanRegistry beanRegistry;
    private final List<BeanConfiguration> remainingBeanConfigurations = new ArrayList<>();

    /**
     * Creates a new {@link BeanConfigurationsBeansCollector} that collects beans in the given {@link BeanRegistry}.
     *
     * @param beanRegistry the {@link BeanRegistry} to collect beans in
     */
    BeanConfigurationsBeansCollector(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry;
    }

    /**
     * Collects beans from the given {@link BeanConfiguration}s and registers them at {@link BeanRegistry}.
     *
     * @param beanConfigurations the {@link BeanConfiguration}s that define the beans
     */
    void collectBeans(List<? extends BeanConfiguration> beanConfigurations) {
        remainingBeanConfigurations.addAll(beanConfigurations);
        collectRemainingBeans();
        applyBeanRegistryPostProcessors();
    }

    private void collectRemainingBeans() {
        while (!remainingBeanConfigurations.isEmpty()) {
            BeanConfiguration beanConfiguration = remainingBeanConfigurations.remove(0);
            beanConfiguration.defineBeans(this);
        }
    }

    private void applyBeanRegistryPostProcessors() {
        for (BeanRegistryPostProcessor postProcessor : beanRegistry.lookUpBeans(BeanRegistryPostProcessor.class)) {
            postProcessor.postProcess(beanRegistry);
        }
    }

    /**
     * Callback-method for {@link BeanConfiguration}s to define their beans.
     *
     * @param bean the bean
     */
    public void defineBean(Object bean) {
        beanRegistry.registerBean(bean);
    }

    /**
     * Callback-method for {@link BeanConfiguration}s to define their beans with an explicit name.
     * <p>
     * Defining a bean with an explicit name allows to override a bean with the same name. Additionally defining an
     * explicit name can speed-up the lookup of the bean if the lookup is done by name.
     *
     * @param name the name of the bean
     * @param bean the bean
     */
    public void defineBean(String name, Object bean) {
        beanRegistry.registerBean(name, bean);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally, if no such bean can be found, but there are further {@link BeanConfiguration}s to process, they are
     * processed until a matching bean is found. This is likely to happen when the lookup happens from a
     * {@link BeanConfiguration} to resolve a dependency to a bean defined by another {@link BeanConfiguration}.
     */
    @Override
    public <T> T lookUpBean(String name, Class<T> type) {
        T bean = beanRegistry.lookUpBean(name, type);
        while (bean == null && !remainingBeanConfigurations.isEmpty()) {
            remainingBeanConfigurations.remove(0).defineBeans(this);
            bean = beanRegistry.lookUpBean(name, type);
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally, if no such bean can be found, but there are further {@link BeanConfiguration}s to process, they are
     * processed until a matching bean is found. This is likely to happen when the lookup happens from a
     * {@link BeanConfiguration} to resolve a dependency to a bean defined by another {@link BeanConfiguration}.
     */
    @Override
    public <T> T lookUpBean(Class<T> type) {
        T bean = beanRegistry.lookUpBean(type);
        while (bean == null && !remainingBeanConfigurations.isEmpty()) {
            remainingBeanConfigurations.remove(0).defineBeans(this);
            bean = beanRegistry.lookUpBean(type);
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In order to find all beans, the {@link BeanConfigurationsBeansCollector} will try to collect all beans from
     * remaining {@link BeanConfiguration}s first.
     */
    @Override
    public <T> List<T> lookUpBeans(Class<T> type) {
        collectRemainingBeans();
        return beanRegistry.lookUpBeans(type);
    }
}
