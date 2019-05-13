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

    private T bean = null;

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
     * {@inheritDoc}
     * <p>
     * As long as there is no suitable bean for this dependency is available, this dependency is
     * {@link Fulfillment#UNFULFILLED_OPTIONAL}. After a suitable bean could be obtained, this dependency is
     * {@link Fulfillment#FULFILLED} and the {@link BeansProvider} will no linger be queried.
     */
    @Override
    public Fulfillment fulfill(BeansProvider beansProvider) {
        if (bean == null) {
            bean = (name != null ? beansProvider.lookUpBean(name, type) : beansProvider.lookUpBean(type));
        }

        return (bean != null ? Fulfillment.FULFILLED : Fulfillment.UNFULFILLED_OPTIONAL);
    }

    @Override
    public Optional<T> get() {
        return Optional.ofNullable(bean);
    }
}
