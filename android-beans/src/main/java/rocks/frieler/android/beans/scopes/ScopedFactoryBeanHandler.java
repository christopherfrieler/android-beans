package rocks.frieler.android.beans.scopes;

/**
 * Interface for classes handling {@link ScopedFactoryBean}s of the corresponding scope.
 */
public interface ScopedFactoryBeanHandler {

    /**
     * Returns the name of the scope.
     *
     * @return the name of the scope
     */
    String getName();

    /**
     * Checks whether the scope is active, i.e. if beans of this scope are currently available.
     *
     * @return {@code true} or {@code false} whether the scope is active
     */
    boolean isActive();

    /**
     * Returns the bean defined by the given {@link ScopedFactoryBean}.
     * <p>
     * The bean can be an existing one already lining in this scope, if so. Otherwise a new bean is created by the
     * {@link ScopedFactoryBean factoryBean}.
     * <p>
     * Note: This method may only be called, when the scope {@link #isActive() is active}.
     *
     * @param name the name of the bean, which is equal to the name of the {@link ScopedFactoryBean factory bean}
     * @param factoryBean the {@link ScopedFactoryBean factory bean} to produce the desired bean
     * @param <T> the type of the bean
     * @return the existing bean in this scope or a new one
     */
    <T> T getBean(String name, ScopedFactoryBean<T> factoryBean);
}
