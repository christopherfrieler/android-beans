package rocks.frieler.android.beans.scopes;

import java8.util.function.Function;

/**
 * Decorator for {@link ScopedFactoryBean}s to enhance their behaviour.
 *
 * @param <T> the type of bean produced
 */
public class ScopedFactoryBeanDecorator<T> implements ScopedFactoryBean<T> {
    private Function<T, T> postProcessing;

    public static <T> ScopedFactoryBeanDecorator<T> decorate(ScopedFactoryBean<T> scopedFactoryBean) {
        return new ScopedFactoryBeanDecorator<>(scopedFactoryBean);
    }

    private final ScopedFactoryBean<T> delegate;

    private ScopedFactoryBeanDecorator(ScopedFactoryBean<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getScope() {
        return delegate.getScope();
    }

    @Override
    public Class<T> getBeanType() {
        return delegate.getBeanType();
    }

    @Override
    public T produceBean() {
        T bean = delegate.produceBean();

        if (postProcessing != null) {
            bean = postProcessing.apply(bean);
        }

        return bean;
    }

    public ScopedFactoryBeanDecorator<T> withPostProcessing(Function<T, T> postProcessing) {
        this.postProcessing = postProcessing;
        return this;
    }
}
