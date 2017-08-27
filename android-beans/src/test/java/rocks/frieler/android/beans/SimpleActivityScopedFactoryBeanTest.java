package rocks.frieler.android.beans;

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
public class SimpleActivityScopedFactoryBeanTest {
    @Test
    public void testGetTypeReturnsConfiguredType() {
        SimpleActivityScopedFactoryBean<SimpleActivityScopedFactoryBeanTest> factoryBean
                = new SimpleActivityScopedFactoryBean<>(SimpleActivityScopedFactoryBeanTest.class, null);

        assertThat(factoryBean.getType(), is(equalTo(SimpleActivityScopedFactoryBeanTest.class)));
    }

    @Test
    public void testProduceBeanCallsConfiguredProducer() {
        Supplier<SimpleActivityScopedFactoryBeanTest> producer = mock(Supplier.class);
        when(producer.get()).thenReturn(this);

        SimpleActivityScopedFactoryBean<SimpleActivityScopedFactoryBeanTest> factoryBean
                = new SimpleActivityScopedFactoryBean<>(SimpleActivityScopedFactoryBeanTest.class, producer);

        assertThat(factoryBean.produceBean(), is(this));
    }
}
