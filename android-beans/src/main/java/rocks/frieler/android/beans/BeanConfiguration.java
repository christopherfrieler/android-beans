package rocks.frieler.android.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract super-class to define beans for the context of an application.
 * <p>
 * {@link BeanConfiguration}s must provide a public constructor, which takes the {@link android.content.Context} or no
 * arguments, to be instantiated.
 * <p>
 * Usage example:
 * <pre>
 *     // 1. extend BeanConfiguration:
 *     public class MyBeanConfiguration extends BeanConfiguration {
 *
 *         // 2. declare dependencies to other beans using the require-methods:
 *         private final BeanDependency&lt;AnotherBean&gt; dependency = requireBean(AnotherBean.class);
 *
 *         // 3. define beans:
 *         public void defineBeans(BeansCollector beansCollector) {
 *             beansCollector.defineBean(new MyBean(dependency.get()));
 *         }
 *     }
 * </pre>
 *
 * @author Christopher Frieler
 */
public abstract class BeanConfiguration {
    private List<BeanDependency<?>> beanDependencies = new ArrayList<>();

    /**
     * Creates and registers a {@link BeanDependency} on a bean with the given name and of the given type.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the type of the desired bean
     * @return the {@link BeanDependency} on the desired bean
     */
    protected final <T> BeanDependency<T> requireBean(String name, Class<T> type) {
        SingleBeanDependency<T> beanDependency = new SingleBeanDependency<>(name, type);
        beanDependencies.add(beanDependency);
        return beanDependency;
    }

    /**
     * Creates and registers a {@link BeanDependency} on a bean of the given type.
     *
     * @param type the type of the desired bean
     * @param <T> the type of the desired bean
     * @return the {@link BeanDependency} on the desired bean
     */
    protected final <T> BeanDependency<T> requireBean(Class<T> type) {
        SingleBeanDependency<T> beanDependency = new SingleBeanDependency<>(type);
        beanDependencies.add(beanDependency);
        return beanDependency;
    }

    /**
     * Creates and registers a {@link BeanDependency} on the beans of the given type.
     *
     * @param type the type of the desired bean
     * @param <T> the type of the desired bean
     * @return the {@link BeanDependency} on the desired bean
     */
    protected final <T> BeanDependency<List<T>> requireBeans(Class<T> type) {
        BeansOfTypeDependency<T> beansDependency = new BeansOfTypeDependency<>(type);
        beanDependencies.add(beansDependency);
        return beansDependency;
    }

    /**
     * Allows to express {@link BeanDependency}s that must be fulfilled for this {@link BeanConfiguration} to define its
     * beans.
     * <p>
     * By default this method returns all {@link BeanDependency}s registered by the require-methods such as
     * {@link #requireBean(String, Class)}
     *
     * @return the {@link BeanDependency}s of this {@link BeanConfiguration}
     */
    protected List<BeanDependency<?>> getDependencies() {
        return beanDependencies;
    }

    /**
     * Checks, if this {@link BeanConfiguration} is ready to define its beans, i.e. tries to fulfill all its
     * {@link BeanDependency}s returned by {@link #getDependencies()}.
     *
     * @param beansProvider the {@link BeansProvider} to fulfill {@link BeanDependency}s
     * @return {@code true}, if all {@link BeanDependency}s are fulfilled
     */
    public final boolean isReadyToDefineBeans(BeansProvider beansProvider) {
        for (BeanDependency dependency : getDependencies()) {
            if (!dependency.fulfill(beansProvider)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Defines beans for the context of this application by calling
     * {@link BeanConfigurationsBeansCollector#defineBean(String, Object)} or
     * {@link BeanConfigurationsBeansCollector#defineBean(Object)}.
     * <p>
     * Other beans can by obtained by declaring {@link BeanDependency}s to satisfy dependencies of the defined beans
     * through {@link #getDependencies()}. Hence, the require-methods that register {@link BeanDependency}s must not be
     * called inside this method; this must be done earlier. And take care not to create cyclic dependencies between
     * {@link BeanConfiguration}s which are unresolvable.
     * <p>
     * This method must not be called before {@link #isReadyToDefineBeans(BeansProvider)} returned
     * {@code true}.
     *
     * @param beansCollector the {@link BeanConfigurationsBeansCollector} that collects the beans
     *
     * @see BeanConfigurationsBeansCollector#defineBean(String, Object)
     * @see BeanConfigurationsBeansCollector#defineBean(Object)
     */
    public abstract void defineBeans(BeansCollector beansCollector);
}
