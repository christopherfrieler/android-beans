package rocks.frieler.android.beans;

import java.util.List;

import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;

/**
 * Static facade-class to access the beans.
 */
public final class Beans {
    private static BeansProvider beansProvider;

    static void setBeans(BeansProvider beansProvider) {
        Beans.beansProvider = beansProvider;
    }

    /**
     * Looks up the bean with the given name and type in the {@link BeansProvider} of this application.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or {@code null}
     *
     * @see BeansProvider#lookUpBean(String, Class)
     */
    public static <T> T lookUpBean(String name, Class<T> type) {
        return beansProvider.lookUpBean(name, type);
    }

    /**
     * Looks up a bean of the given type in the {@link BeansProvider} of this application.
     *
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return a bean of the given type or {@code null}
     *
     * @see BeansProvider#lookUpBean(Class)
     */
    public static <T> T lookUpBean(Class<T> type) {
        return beansProvider.lookUpBean(type);
    }

    /**
     * Looks up all beans of the given type in the {@link BeansProvider} of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     *
     * @see BeansProvider#lookUpBeans(Class)
     */
    public static <T> List<T> lookUpBeans(Class<T> type) {
        return beansProvider.lookUpBeans(type);
    }

    private Beans() {
    }

    /**
     * Initializer for {@link Beans}.
     * <p>
     * Initializes {@link Beans} with a {@link BeanRegistry} and allows to configure it.
     */
    public static final class Initializer {
        private BeanRegistry beanRegistry = new BeanRegistry();

        /**
         * Adds a bean-scope expressed by the given {@link ScopedFactoryBeanHandler}.
         *
         * @param scopedFactoryBeanHandler the {@link ScopedFactoryBeanHandler} to add
         * @return the {@link Initializer} itself
         *
         * @see BeanRegistry#addBeanScope(ScopedFactoryBeanHandler)
         */
        public Initializer addScope(ScopedFactoryBeanHandler scopedFactoryBeanHandler) {
            beanRegistry.addBeanScope(scopedFactoryBeanHandler);
            return this;
        }

        /**
         * Collects beans from the given {@link BeanConfiguration}s.
         *
         * @param beanConfigurations the BeanConfigurations that define the beans
         * @return the {@link Initializer} itself
         *
         * @see BeanConfigurationsBeansCollector#collectBeans(List)
         */
        public Initializer collectBeans(List<? extends BeanConfiguration> beanConfigurations) {
            new BeanConfigurationsBeansCollector(beanRegistry).collectBeans(beanConfigurations);
            return this;
        }

        /**
         * Initializes {@link Beans} with the applied configuration.
         */
        public void initialize() {
            setBeans(beanRegistry);
        }
    }
}
