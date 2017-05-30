package rocks.frieler.android.beans;

/**
 * {@link RuntimeException} thrown, when beans could not be instantiated.
 *
 * @author Christopher Frieler
 */
public class BeanInstantiationException extends RuntimeException {
    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
