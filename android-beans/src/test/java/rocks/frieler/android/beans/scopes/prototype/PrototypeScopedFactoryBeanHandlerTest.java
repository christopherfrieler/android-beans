package rocks.frieler.android.beans.scopes.prototype;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE;

@RunWith(MockitoJUnitRunner.class)
public class PrototypeScopedFactoryBeanHandlerTest {
    private PrototypeScopedFactoryBeanHandler prototypeScopedFactoryBeanHandler = new PrototypeScopedFactoryBeanHandler();

    @Mock
    private PrototypeScopedFactoryBean<Object> scopedFactoryBean;

    @Test
    public void testGetNameReturnsPrototypeScope() {
        assertThat(prototypeScopedFactoryBeanHandler.getName(), is(PROTOTYPE_SCOPE));
    }

    @Test
    public void testIsAlwaysActive() {
        assertThat(prototypeScopedFactoryBeanHandler.isActive(), is(true));
    }

    @Test
    public void testGetBeanLetsFactoryBeanProduceBean() {
        final Object bean = new Object();
        when(scopedFactoryBean.produceBean()).thenReturn(bean);

        final Object beanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);

        assertThat(beanInstance, is(sameInstance(bean)));
    }

    @Test
    public void testGetBeanProducesNewBeanEveryTime() {
        final Object firstBean = new Object();
        final Object secondBean = new Object();
        when(scopedFactoryBean.produceBean()).thenReturn(firstBean, secondBean);

        final Object firstBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);
        final Object secondBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean);

        verify(scopedFactoryBean, times(2)).produceBean();
        assertThat(firstBeanInstance, is(sameInstance(firstBean)));
        assertThat(secondBeanInstance, is(sameInstance(secondBean)));
    }
}