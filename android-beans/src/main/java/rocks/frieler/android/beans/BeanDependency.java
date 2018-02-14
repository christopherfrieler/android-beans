package rocks.frieler.android.beans;

/**
 * Interface to express a dependency on one ore more beans to be available from a {@link BeansProvider}.
 *
 * @param <T> the target-type of this {@link BeanDependency}
 *
 * @author Christopher Frieler
 */
public interface BeanDependency<T> {

    /**
     * Tries to fulfill this {@link BeanDependency} with beans from the given {@link BeansProvider}. Returns
     * {@code true} if the necessary beans were available, {@code false} otherwise.
     *
     * @param beansProvider the {@link BeansProvider} to obtain beans from
     * @return {@code true} if this {@link BeanDependency} could be fulfilled
     */
    boolean fulfill(BeansProvider beansProvider);

    /**
     * Returns the target of this {@link BeanDependency} after it was fulfilled.
     *
     * @return the target of this {@link BeanDependency}
     */
    T get();
}
