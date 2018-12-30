package rocks.frieler.android.beans.scopes.singleton;

import java.util.HashMap;
import java.util.Map;

import rocks.frieler.android.beans.scopes.ScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;

/**
 * {@link ScopedFactoryBeanHandler} for the {@value #SINGLETON_SCOPE}-scope.
 * <p>
 * It produces a new bean-instance only for the first time and re-uses it forever.
 */
public class SingletonScopedFactoryBeanHandler implements ScopedFactoryBeanHandler {
    public static final String SINGLETON_SCOPE = "singleton";

    private final Map<String, Object> beans = new HashMap<>();

    @Override
    public String getName() {
        return SINGLETON_SCOPE;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public <T> T getBean(String name, ScopedFactoryBean<T> factoryBean) {
        @SuppressWarnings("unchecked") T bean = (T) beans.get(name);

        if (bean == null) {
            bean = factoryBean.produceBean();
            beans.put(name, bean);
        }

        return bean;
    }
}
