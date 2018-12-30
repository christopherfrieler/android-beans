package rocks.frieler.android.beans.scopes.prototype;

import rocks.frieler.android.beans.scopes.ScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;

/**
 * {@link ScopedFactoryBeanHandler} for the {@value #PROTOTYPE_SCOPE}-scope.
 * <p>
 * It produces a new bean-instance every time.
 */
public class PrototypeScopedFactoryBeanHandler implements ScopedFactoryBeanHandler {
    public static final String PROTOTYPE_SCOPE = "prototype";

    @Override
    public String getName() {
        return PROTOTYPE_SCOPE;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public <T> T getBean(String name, ScopedFactoryBean<T> factoryBean) {
        return factoryBean.produceBean();
    }
}
