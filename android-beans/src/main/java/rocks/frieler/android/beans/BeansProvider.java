package rocks.frieler.android.beans;

import java.util.List;

/**
 * Interface for classes that provide the beans of an application.
 * <p>
 * Beans are singleton simple Java objects, that get registered with a name and can be looked up either by their name or
 * type to inject the as dependencies (DI).
 *
 * @author Christopher Frieler
 */
public interface BeansProvider {
    /**
     * Looks up the bean with the given name and type.
     * <p>
     * If no bean with that name is registered or the registered bean is not assignable to the required type,
     * {@code null} is returned.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or {@code null}
     */
    <T> T lookUpBean(String name, Class<T> type);

    /**
     * Looks up a bean of the given type.
     * <p>
     * If no bean is assignable to the required type, {@code null} is returned. If multiple beans are assignable to the
     * required type, the first match is returned.
     *
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return a bean of the given type or {@code null}
     */
    <T> T lookUpBean(Class<T> type);

    /**
     * Looks up all beans of the given type in the {@link BeanRegistry} of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     */
    <T> List<T> lookUpBeans(Class<T> type);
}
