package rocks.frieler.android.beans;

import java.util.List;

/**
 * Static facade-class to access the beans.
 */
public final class Beans {
    private static BeansProvider beansProvider;

    static void setBeans(BeansProvider beansProvider) {
        Beans.beansProvider = beansProvider;
    }

    /**
     * Looks up the bean with the given name and type in the {@link BeansProvider} of this application.
     *
     * @param name the name of the desired bean
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return the named bean or {@code null}
     *
     * @see BeansProvider#lookUpBean(String, Class)
     */
    public static <T> T lookUpBean(String name, Class<T> type) {
        return beansProvider.lookUpBean(name, type);
    }

    /**
     * Looks up a bean of the given type in the {@link BeansProvider} of this application.
     *
     * @param type the type of the desired bean
     * @param <T> the bean-type
     * @return a bean of the given type or {@code null}
     *
     * @see BeansProvider#lookUpBean(Class)
     */
    public static <T> T lookUpBean(Class<T> type) {
        return beansProvider.lookUpBean(type);
    }

    /**
     * Looks up all beans of the given type in the {@link BeansProvider} of this application.
     *
     * @param type the type of the desired beans
     * @param <T> the bean-type
     * @return the beans of the given type or an empty list
     *
     * @see BeansProvider#lookUpBeans(Class)
     */
    public static <T> List<T> lookUpBeans(Class<T> type) {
        return beansProvider.lookUpBeans(type);
    }

    private Beans() {
    }
}
