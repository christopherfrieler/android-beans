package rocks.frieler.android.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link BeansProvider} that holds and provides beans which were explicitly registered before.
 *
 * @author Christopher Frieler
 */
public class BeanRegistry implements BeansProvider {
    private final Map<String, Object> beans = new HashMap<>();

    /**
     * Registers the bean with a generated name at this {@link BeanRegistry}.
     *
     * @param bean the bean
     */
    void registerBean(Object bean) {
        registerBean(generateBeanName(bean), bean);
    }

    private String generateBeanName(Object bean) {
        String preferredBeanName = getPreferredBeanName(bean.getClass());
        if (!beans.containsKey(preferredBeanName)) {
            return preferredBeanName;
        }

        int i = 2;
        while (beans.containsKey(preferredBeanName + i)) {
            i++;
        }
        return preferredBeanName + i;
    }

    /**
     * Registers the bean with the given name at this {@link BeanRegistry}.
     * <p>
     * If a bean with this name already exists, it will be replaced.
     *
     * @param name the name of the bean
     * @param bean the bean
     */
    void registerBean(String name, Object bean) {
        beans.put(name, bean);
    }

    @Override
    public <T> T lookUpBean(String name, Class<T> type) {
        Object bean = beans.get(name);
        if (bean != null && type.isAssignableFrom(bean.getClass())) {
            //noinspection unchecked
            return (T) bean;
        } else {
            return null;
        }
    }

    @Override
    public <T> T lookUpBean(Class<T> type) {
        Object beanCandidateByPreferredName = beans.get(getPreferredBeanName(type));
        if (beanCandidateByPreferredName != null && type.isAssignableFrom(beanCandidateByPreferredName.getClass())) {
            //noinspection unchecked
            return (T) beanCandidateByPreferredName;
        }

        for (Object bean : beans.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                //noinspection unchecked
                return (T) bean;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> lookUpBeans(Class<T> type) {
        List<T> matchingBeans = new ArrayList<>();
        for (Object bean : beans.values()) {
            if (type.isAssignableFrom(bean.getClass())) {
                //noinspection unchecked
                matchingBeans.add((T) bean);
            }
        }
        return matchingBeans;
    }

    private static String getPreferredBeanName(Class<?> beanClass) {
        return beanClass.getName();
    }
}
