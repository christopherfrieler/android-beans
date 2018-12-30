package rocks.frieler.android.beans.scopes.singleton;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazy;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE;

@RunWith(MockitoJUnitRunner.class)
public class SingletonScopedFactoryBeanTest {

    @SuppressWarnings("unchecked")
    private Supplier<SingletonScopedFactoryBeanTest> producer = mock(Supplier.class);
    private SingletonScopedFactoryBean<SingletonScopedFactoryBeanTest> factoryBean = lazy(SingletonScopedFactoryBeanTest.class, producer);

    @Test
    public void testGetScopeReturnsSingletonScope() {
        assertThat(factoryBean.getScope(), is(SINGLETON_SCOPE));
    }

    @Test
    public void testGetTypeReturnsConfiguredType() {
        assertThat(factoryBean.getBeanType(), is(equalTo(SingletonScopedFactoryBeanTest.class)));
    }

    @Test
    public void testProduceBeanCallsConfiguredProducer() {
        when(producer.get()).thenReturn(this);

        assertThat(factoryBean.produceBean(), is(this));
    }
}