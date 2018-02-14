package rocks.frieler.android.beans;

import java.util.List;

/**
 * {@link BeanDependency} to express a dependency on the beans of a certain type.
 * <p>
 * Be aware that this dependency cannot guaranty to provide <i>all</i> beans of that type, because the exact runtime
 * type of a bean and whether it is a subtype of the desired type cannot be determined before the bean's construction.
 * Hence this dependency always indicates to be {@link #fulfill(BeansProvider) fulfilled}. Instead the dependency will
 * gather as many beans as possible by obtaining the beans as late as possible.
 *
 * @param <T> the bean-type
 */
public class BeansOfTypeDependency<T> implements BeanDependency<List<T>> {
    private final Class<T> type;
    private BeansProvider beansProvider = null;

    /**
     * Creates a new {@link BeansOfTypeDependency} to beans of the given type.
     *
     * @param type the type of beans
     */
    public BeansOfTypeDependency(Class<T> type) {
        this.type = type;
    }

    /**
     * Always returns {@code true}, because it cannot be known if there will be any more beans of the desired type
     * defined in the future. The given {@link BeansProvider} will be used to obtain the beans lazily when needed.
     */
    @Override
    public boolean fulfill(BeansProvider beansProvider) {
        this.beansProvider = beansProvider;
        return true;
    }

    /**
     * Obtains all beans of the desired type from the {@link BeansProvider} this dependency was
     * {@link #fulfill(BeansProvider) fulfilled} with.
     */
    @Override
    public List<T> get() {
        if(beansProvider == null) {
            throw new IllegalStateException("BeansOfTypeDependency has not been fulfilled yet.");
        }
        return beansProvider.lookUpBeans(type);
    }
}
