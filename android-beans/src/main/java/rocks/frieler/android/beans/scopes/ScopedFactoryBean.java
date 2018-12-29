package rocks.frieler.android.beans.scopes;

import android.app.Activity;

/**
 * Interface for factory-beans, that produce the actual bean living in a limited scope, when needed.
 *
 * @param <T> the type of bean produced
 */
public interface ScopedFactoryBean<T> {

    /**
     * Returns the name of the scope, where the bean produced by this {@link ScopedFactoryBean} lives in.
     *
     * @return the name of the scope
     */
    String getScope();

    /**
     * Returns the type of the bean produced by this {@link ScopedFactoryBean}.
     *
     * @return the type of the bean
     */
    Class<T> getBeanType();

    /**
     * Produces a new bean of type {@link T} corresponding to the given {@link Activity}.
     *
     * @return a new bean of type {@link T} corresponding to the given {@link Activity}
     */
    T produceBean();
}
