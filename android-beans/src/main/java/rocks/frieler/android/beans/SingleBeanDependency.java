package rocks.frieler.android.beans;

/**
 * {@link BeanDependency} to express a dependency on a single bean.
 *
 * @param <T> the bean-type
 */
public class SingleBeanDependency<T> implements BeanDependency<T> {
    private final String name;
    private final Class<? extends T> type;
    private T bean = null;


    /**
     * Creates a new {@link BeanDependency} to a bean of the given type.
     *
     * @param type the type of the bean
     */
    public SingleBeanDependency(Class<? extends T> type) {
        this(null, type);
    }

    /**
     * Creates a new {@link BeanDependency} to a bean with the given name and of the given type.
     *
     * @param name the name of the bean
     * @param type the type of the bean
     */
    public SingleBeanDependency(String name, Class<? extends T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Fulfillment fulfill(BeansProvider beansProvider) {
        if (bean == null) {
            bean = (name == null ? beansProvider.lookUpBean(type) : beansProvider.lookUpBean(name, type));
        }
        return (bean != null ? Fulfillment.FULFILLED : Fulfillment.UNFULFILLED);
    }

    @Override
    public T get() {
        return bean;
    }
}
