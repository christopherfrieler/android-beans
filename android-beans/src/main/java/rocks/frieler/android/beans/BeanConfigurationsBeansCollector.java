package rocks.frieler.android.beans;

import java.util.LinkedList;
import java.util.List;

import static rocks.frieler.android.beans.BeanConfiguration.Readiness.*;

/**
 * The {@link BeanConfigurationsBeansCollector} collects the beans defined by {@link BeanConfiguration}s in a
 * {@link BeanRegistry}.
 * <p>
 * The {@link BeanConfigurationsBeansCollector} also implements the {@link BeansProvider}-interface by providing the
 * beans that are already registered in the {@link BeanRegistry}. In addition the
 * {@link BeanConfigurationsBeansCollector} may process further {@link BeanConfiguration}s to fulfill bean-lookups for
 * beans that are not present yet.
 *
 * @author Christopher Frieler
 */
public class BeanConfigurationsBeansCollector implements BeansCollector, BeansProvider {
    private final BeanRegistry beanRegistry;
    private final List<BeanConfiguration> remainingBeanConfigurations = new LinkedList<>();

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
        collectRemainingBeans(true);
        if (!remainingBeanConfigurations.isEmpty()) {
            throw new BeanInstantiationException("bean-configurations seem to have a cyclic dependency.");
        }
        applyBeanRegistryPostProcessors();
    }

    private void collectRemainingBeans(final boolean mayIncludeDelayed) {
        int limit = remainingBeanConfigurations.size();
        boolean includingDelayed = false;
        while (limit > 0) {
            BeanConfiguration beanConfiguration = remainingBeanConfigurations.remove(0);

            if (beanConfiguration.isReadyToDefineBeans(this) == READY) {
                beanConfiguration.defineBeans(this);
                limit = remainingBeanConfigurations.size();
            } else if (includingDelayed && beanConfiguration.isReadyToDefineBeans(this) == DELAY) {
                beanConfiguration.defineBeans(this);
                includingDelayed = false;
                limit = remainingBeanConfigurations.size();
            } else {
                remainingBeanConfigurations.add(beanConfiguration);
                limit--;
            }

            if (limit == 0 && mayIncludeDelayed && !includingDelayed && !remainingBeanConfigurations.isEmpty()) {
                includingDelayed = true;
                limit = remainingBeanConfigurations.size();
            }
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
    @Override
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
    @Override
    public void defineBean(String name, Object bean) {
        beanRegistry.registerBean(name, bean);
    }

    /**
     * Registers the given {@link BeanPostProcessor} at the underlying {@link BeanRegistry}.
     *
     * @param beanPostProcessor the new {@link BeanPostProcessor}
     *
     * @see BeanRegistry#registerBeanPostProcessor(BeanPostProcessor)
     */
    @Override
    public void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanRegistry.registerBeanPostProcessor(beanPostProcessor);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In order to find the bean, the {@link BeanConfigurationsBeansCollector} will attempt to collect more beans from
     * remaining {@link BeanConfiguration.Readiness#READY ready} {@link BeanConfiguration}s first.
     */
    @Override
    public <T> T lookUpBean(String name, Class<T> type) {
        collectRemainingBeans(false);
        return beanRegistry.lookUpBean(name, type);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In order to find the bean, the {@link BeanConfigurationsBeansCollector} will attempt to collect more beans from
     * remaining {@link BeanConfiguration.Readiness#READY ready} {@link BeanConfiguration}s first.
     */
    @Override
    public <T> T lookUpBean(Class<T> type) {
        collectRemainingBeans(false);
        return beanRegistry.lookUpBean(type);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In order to find all beans, the {@link BeanConfigurationsBeansCollector} will attempt to collect more beans from
     * remaining {@link BeanConfiguration.Readiness#READY ready} {@link BeanConfiguration}s first.
     */
    @Override
    public <T> List<T> lookUpBeans(Class<T> type) {
        collectRemainingBeans(false);
        return beanRegistry.lookUpBeans(type);
    }
}
