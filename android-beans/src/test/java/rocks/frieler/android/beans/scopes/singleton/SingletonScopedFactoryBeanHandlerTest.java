package rocks.frieler.android.beans.scopes.singleton;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE;

@RunWith(MockitoJUnitRunner.class)
public class SingletonScopedFactoryBeanHandlerTest {
    private SingletonScopedFactoryBeanHandler singletonScopedFactoryBeanHandler = new SingletonScopedFactoryBeanHandler();

    @Mock
    private SingletonScopedFactoryBean<Object> scopedFactoryBean;

    @Test
    public void testGetNameReturnsPrototypeScope() {
        assertThat(singletonScopedFactoryBeanHandler.getName(), is(SINGLETON_SCOPE));
    }

    @Test
    public void testIsAlwaysActive() {
        assertThat(singletonScopedFactoryBeanHandler.isActive(), is(true));
    }

    @Test
    public void testGetBeanLetsFactoryBeanProduceBean() {
        final Object bean = new Object();
        when(scopedFactoryBean.produceBean()).thenReturn(bean);

        final Object beanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);

        assertThat(beanInstance, is(sameInstance(bean)));
    }

    @Test
    public void testGetBeanProducesNewBeanEveryTime() {
        final Object bean = new Object();
        when(scopedFactoryBean.produceBean()).thenReturn(bean);

        final Object firstBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);
        final Object secondBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);

        verify(scopedFactoryBean, times(1)).produceBean();
        assertThat(firstBeanInstance, is(sameInstance(bean)));
        assertThat(secondBeanInstance, is(sameInstance(bean)));
    }
}