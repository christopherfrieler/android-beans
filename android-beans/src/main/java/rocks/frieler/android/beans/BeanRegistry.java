package rocks.frieler.android.beans;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import rocks.frieler.android.beans.scopes.ScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanDecorator;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBeanHandler;
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBeanHandler;


/**
 * {@link BeansProvider} that holds and provides beans which were explicitly registered before.
 * <p>
 * The {@link BeanRegistry} supports the {@value SingletonScopedFactoryBeanHandler#SINGLETON_SCOPE} and
 * {@value PrototypeScopedFactoryBeanHandler#PROTOTYPE_SCOPE} by default.
 */
public class BeanRegistry implements BeansProvider {
    private final Map<String, Object> beans = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new LinkedList<>();

    private final Map<String, ScopedFactoryBeanHandler> beanScopes = new TreeMap<>();

    BeanRegistry() {
        addBeanScope(new SingletonScopedFactoryBeanHandler());
        addBeanScope(new PrototypeScopedFactoryBeanHandler());
    }

    /**
     * Adds a bean-scope expressed by the given {@link ScopedFactoryBeanHandler} to this {@link BeanRegistry}.
     *
     * @param scopedFactoryBeanHandler the {@link ScopedFactoryBeanHandler} to add
     */
    void addBeanScope(ScopedFactoryBeanHandler scopedFactoryBeanHandler) {
        beanScopes.put(scopedFactoryBeanHandler.getName(), scopedFactoryBeanHandler);
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
        if (bean instanceof ScopedFactoryBean) {
            beanClass = ((ScopedFactoryBean) bean).getBeanType();
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
     * The bean will be post-processed by all registered {@link BeanPostProcessor}s.
     * <p>
     * If a bean with this name already exists, it will be replaced.
     *
     * @param name the name of the bean
     * @param bean the bean
     */
    void registerBean(String name, Object bean) {
        bean = postProcessBean(name, bean);
        beans.put(name, bean);
    }

    /**
     * Registers the given {@link BeanPostProcessor}.
     * <p>
     * The {@link BeanPostProcessor} will get all beans already registered to post-process immediately and all beans
     * registered subsequently.
     *
     * @param beanPostProcessor the new {@link BeanPostProcessor}
     */
    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);

        for (Entry<String, Object> beanEntry : beans.entrySet()) {
            beanEntry.setValue(beanPostProcessor.postProcessBean(beanEntry.getKey(), beanEntry.getValue()));
        }
    }

    private <T> T postProcessBean(String name, T bean) {
        for (BeanPostProcessor postProcessor : beanPostProcessors) {
            bean = postProcessor.postProcessBean(name, bean);
        }
        return bean;
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

        for (Entry<String, Object> beanCandidate : beans.entrySet()) {
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
        for (Entry<String, Object> beanCandidate : beans.entrySet()) {
            T bean = resolveBeanFromCandidate(beanCandidate.getKey(), type, beanCandidate.getValue());
            if (bean != null) {
                matchingBeans.add(bean);
            }
        }
        return matchingBeans;
    }

    @Nullable
    private <T> T resolveBeanFromCandidate(String name, Class<T> type, Object beanCandidate) {
        if (beanCandidate instanceof ScopedFactoryBean && type.isAssignableFrom(((ScopedFactoryBean<?>)beanCandidate).getBeanType())) {
            @SuppressWarnings("unchecked") final ScopedFactoryBean<T> factoryBean = (ScopedFactoryBean<T>) beanCandidate;
            final ScopedFactoryBeanHandler scopedFactoryBeanHandler = beanScopes.get(factoryBean.getScope());
            if (scopedFactoryBeanHandler != null && scopedFactoryBeanHandler.isActive()) {
                return scopedFactoryBeanHandler.getBean(name, ScopedFactoryBeanDecorator.Companion.decorate(factoryBean).withPostProcessing((bean) -> postProcessBean(name, bean)));
            }
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
