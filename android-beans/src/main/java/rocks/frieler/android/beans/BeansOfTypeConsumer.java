package rocks.frieler.android.beans;

import java8.util.function.Consumer;


/**
 * {@link BeanPostProcessor} to consume all beans of a certain type (and all its subtypes).
 * <p>
 * A common use-case is to collect all beans implementing a certain interface and inject them lazily into another bean.
 *
 * @param <Type> the type of beans to post-process
 */
public class BeansOfTypeConsumer<Type> implements BeanPostProcessor {
    private final Class<Type> type;
    private final Consumer<Type> consumer;

    public BeansOfTypeConsumer(Class<Type> type, Consumer<Type> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    @Override
    public <T> T postProcessBean(String name, T bean) {
        if (type.isAssignableFrom(bean.getClass())) {
            //noinspection unchecked
            consumer.accept((Type) bean);
        }

        return bean;
    }
}
