package rocks.frieler.android.beans;

import java8.util.Optional;

/**
 * {@link BeanDependency} to express an optional dependency on a single bean.
 * <p>
 * This kind of dependency always indicates to be {@link #fulfill(BeansProvider) fulfilled} and will try to provide the
 * wanted by obtaining it from the {@link BeansProvider} as late as possible.
 *
 * @param <T> the bean-type
 */
public class OptionalSingleBeanDependency<T> implements BeanDependency<Optional<T>> {
    private final String name;
    private final Class<? extends T> type;

    private BeansProvider beansProvider;

    /**
     * Creates a new optional {@link BeanDependency} to a bean of the given type.
     *
     * @param type the type of the bean
     */
    public OptionalSingleBeanDependency(Class<? extends T> type) {
        this(null, type);
    }

    /**
     * Creates a new optional {@link BeanDependency} to a bean with the given name and of the given type.
     *
     * @param name the name of the bean
     * @param type the type of the bean
     */
    public OptionalSingleBeanDependency(String name, Class<? extends T> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Always returns {@code true}, because the wanted bean is optional. The given {@link BeansProvider} will be used
     * to obtain the bean lazily when requested.
     */
    @Override
    public boolean fulfill(BeansProvider beansProvider) {
        this.beansProvider = beansProvider;
        return true;
    }

    /**
     * Tries to obtains the bean of the desired type and - if specified - name from the {@link BeansProvider} this
     * dependency was {@link #fulfill(BeansProvider) fulfilled} with.
     */
    @Override
    public Optional<T> get() {
        if(beansProvider == null) {
            throw new IllegalStateException("OptionalSingleBeanDependency has not been fulfilled yet.");
        }

        T bean = name == null ? beansProvider.lookUpBean(type) : beansProvider.lookUpBean(name, type);
        return Optional.ofNullable(bean);
    }
}
