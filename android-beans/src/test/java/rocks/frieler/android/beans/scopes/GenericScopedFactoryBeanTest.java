package rocks.frieler.android.beans.scopes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericScopedFactoryBeanTest {

    private static final String SCOPE = "aScope";
    @SuppressWarnings("unchecked")
    private Supplier<GenericScopedFactoryBeanTest> producer = mock(Supplier.class);
    private GenericScopedFactoryBean<GenericScopedFactoryBeanTest> factoryBean = new GenericScopedFactoryBean<>(SCOPE, GenericScopedFactoryBeanTest.class, producer);

    @Test
    public void testGetScopeReturnsConfiguredScope() {
        assertThat(factoryBean.getScope(), is(SCOPE));
    }

    @Test
    public void testGetTypeReturnsConfiguredType() {
        assertThat(factoryBean.getBeanType(), is(equalTo(GenericScopedFactoryBeanTest.class)));
    }

    @Test
    public void testProduceBeanCallsConfiguredProducer() {
        when(producer.get()).thenReturn(this);

        assertThat(factoryBean.produceBean(), is(this));
    }
}