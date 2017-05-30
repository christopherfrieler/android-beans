package rocks.frieler.android.beans;

/**
 * Interface for classes that define beans for the context of this application.
 * <p>
 * {@link BeanConfiguration}s must provide a public constructor, which takes the {@link android.content.Context} or no
 * arguments, to be instantiated.
 *
 * @author Christopher Frieler
 */
public interface BeanConfiguration {
    /**
     * Defines named beans for the context of this application with
     * {@link BeanConfigurationsBeansCollector#defineBean(String, Object)}.
     * <p>
     * Other beans may be looked using the {@link BeanConfigurationsBeansCollector} to satisfy dependencies of the
     * defined beans. But take care not to create cyclic dependencies between {@link BeanConfiguration}s, which are
     * unresolvable.
     *
     * @param beansCollector the {@link BeanConfigurationsBeansCollector} that collects the beans
     *
     * @see BeanConfigurationsBeansCollector#defineBean(String, Object)
     */
    void defineBeans(BeanConfigurationsBeansCollector beansCollector);
}
