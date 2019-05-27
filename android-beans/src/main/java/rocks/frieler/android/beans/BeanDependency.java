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
     * Tries to fulfill this {@link BeanDependency} with beans from the given {@link BeansProvider} and returns
     * the {@link Fulfillment state of fulfillment}.
     *
     * @param beansProvider the {@link BeansProvider} to obtain beans from
     * @return the {@link Fulfillment}
     */
    Fulfillment fulfill(BeansProvider beansProvider);

    /**
     * Indicates the state of a {@link BeanDependency}.
     *
     * @see #fulfill(BeansProvider)
     */
    enum Fulfillment {
        /**
         * Indicates that the {@link BeanDependency} could not be fulfilled with the available beans.
         */
        UNFULFILLED,

        /**
         * Indicates that the {@link BeanDependency} could not be fulfilled with the available beans, but is optional.
         * Fulfilling it should be retried when there are further beans available. But if it's never fulfilled, that's
         * ok, too.
         */
        UNFULFILLED_OPTIONAL,

        /**
         * Indicates that the {@link BeanDependency} was fulfilled with the available beans.
         */
        FULFILLED;

        static Fulfillment min(Fulfillment aState, Fulfillment anotherState) {
            return aState.ordinal() < anotherState.ordinal() ? aState : anotherState;
        }
    }

    /**
     * Returns the target of this {@link BeanDependency} after it was fulfilled.
     *
     * @return the target of this {@link BeanDependency}
     */
    T get();
}
