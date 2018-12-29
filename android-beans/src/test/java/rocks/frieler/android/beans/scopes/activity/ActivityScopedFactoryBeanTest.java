package rocks.frieler.android.beans.scopes.activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Supplier;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.activityScoped;
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE;

@RunWith(MockitoJUnitRunner.class)
public class ActivityScopedFactoryBeanTest {

    @SuppressWarnings("unchecked")
    private Supplier<ActivityScopedFactoryBeanTest> producer = mock(Supplier.class);
    private ActivityScopedFactoryBean<ActivityScopedFactoryBeanTest> factoryBean = activityScoped(ActivityScopedFactoryBeanTest.class, producer);

    @Test
    public void testGetScopeReturnsActivityScope() {
        assertThat(factoryBean.getScope(), is(ACTIVITY_SCOPE));
    }

    @Test
    public void testGetTypeReturnsConfiguredType() {
        assertThat(factoryBean.getBeanType(), is(equalTo(ActivityScopedFactoryBeanTest.class)));
    }

    @Test
    public void testProduceBeanCallsConfiguredProducer() {
        when(producer.get()).thenReturn(this);

        assertThat(factoryBean.produceBean(), is(this));
    }
}