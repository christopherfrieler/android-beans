package rocks.frieler.android.beans;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
/**
 * {@link BeansProvider} that holds and provides beans which were explicitly registered before.
 *
 * @author Christopher Frieler
 */
public class BeanRegistry implements BeansProvider {
    private final ForegroundActivityHolder foregroundActivityHolder;
    private final Map<String, Object> beans = new HashMap<>();

    BeanRegistry(ForegroundActivityHolder foregroundActivityHolder) {
        this.foregroundActivityHolder = foregroundActivityHolder;
    }

    /**
     * Registers the bean with a generated name at this {@link BeanRegistry}.
     *
     * @param bean the bean
     */
    void registerBean(Object bean) {
        registerBean(generateBeanName(bean), bean);
    }

    private String generateBeanName(Object bean) {
        Class<?> beanClass;
        if (bean instanceof ActivityScopedFactoryBean) {
            beanClass = ((ActivityScopedFactoryBean) bean).getType();
        } else {
            beanClass = bean.getClass();
        }

        String preferredBeanName = getPreferredBeanName(beanClass);
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
        Object beanCandidate = beans.get(name);
        if (beanCandidate != null) {
            T bean = resolveBeanFromCandidate(name, type, beanCandidate);
            if (bean != null) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public <T> T lookUpBean(Class<T> type) {
        String preferredBeanName = getPreferredBeanName(type);
        Object beanCandidateByPreferredName = beans.get(preferredBeanName);
        if (beanCandidateByPreferredName != null) {
            T bean = resolveBeanFromCandidate(preferredBeanName, type, beanCandidateByPreferredName);
            if (bean != null) {
                return bean;
            }
        }

        for (Map.Entry<String, Object> beanCandidate : beans.entrySet()) {
            T bean = resolveBeanFromCandidate(beanCandidate.getKey(), type, beanCandidate.getValue());
            if (bean != null) {
                return bean;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> lookUpBeans(Class<T> type) {
        List<T> matchingBeans = new ArrayList<>();
        for (Map.Entry<String, Object> beanCandidate : beans.entrySet()) {
            T bean = resolveBeanFromCandidate(beanCandidate.getKey(), type, beanCandidate.getValue());
            if (bean != null) {
                matchingBeans.add(bean);
            }
        }
        return matchingBeans;
    }

    @Nullable
    private <T> T resolveBeanFromCandidate(String name, Class<T> type, Object beanCandidate) {
        Activity currentActivity = foregroundActivityHolder.getCurrentActivity();
        if (beanCandidate instanceof ActivityScopedFactoryBean
                && currentActivity instanceof FragmentActivity
                && type.isAssignableFrom(((ActivityScopedFactoryBean) beanCandidate).getType())) {
            //noinspection unchecked
            ActivityScopedFactoryBean<T> factoryBean = (ActivityScopedFactoryBean<T>) beanCandidate;
            return factoryBean.getBean(name, (FragmentActivity) currentActivity);
        }

        if (type.isAssignableFrom(beanCandidate.getClass())) {
            //noinspection unchecked
            return (T) beanCandidate;
        }

        return null;
    }

    public static String getPreferredBeanName(Class<?> beanClass) {
        return beanClass.getName();
    }
}
