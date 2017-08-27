package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ActivityScopedBeanHolderTest {
    private ActivityScopedFactoryBean.ActivityScopedBeanHolder<Object> beanHolder
            = new ActivityScopedFactoryBean.ActivityScopedBeanHolder<>();

    @Test
    public void testOnClearedIgnoresActivityUnawareBean() {
        Object bean = mock(Object.class);
        beanHolder.setBean(bean);

        beanHolder.onCleared();

        verifyZeroInteractions(bean);
    }

    @Test
    public void testOnClearedClearsActivityAwareBean() {
        ActivityAware bean = mock(ActivityAware.class);
        beanHolder.setBean(bean);

        beanHolder.onCleared();

        verify(bean).setActivity(null);
    }
}