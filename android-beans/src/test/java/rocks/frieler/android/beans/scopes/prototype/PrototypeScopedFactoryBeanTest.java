package rocks.frieler.android.beans.scopes.prototype;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE;

@RunWith(MockitoJUnitRunner.class)
public class PrototypeScopedFactoryBeanTest {

    @SuppressWarnings("unchecked")
    private Supplier<PrototypeScopedFactoryBeanTest> producer = mock(Supplier.class);
    private PrototypeScopedFactoryBean<PrototypeScopedFactoryBeanTest> factoryBean = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest.class, producer);

    @Test
    public void testGetScopeReturnsPrototypeScope() {
        assertThat(factoryBean.getScope(), is(PROTOTYPE_SCOPE));
    }

    @Test
    public void testGetTypeReturnsConfiguredType() {
        assertThat(factoryBean.getBeanType(), is(equalTo(PrototypeScopedFactoryBeanTest.class)));
    }

    @Test
    public void testProduceBeanCallsConfiguredProducer() {
        when(producer.get()).thenReturn(this);

        assertThat(factoryBean.produceBean(), is(this));
    }
}