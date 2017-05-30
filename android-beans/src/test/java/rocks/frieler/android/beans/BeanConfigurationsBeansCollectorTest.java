package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanConfigurationsBeansCollectorTest {
    private BeanRegistry beanRegistry = mock(BeanRegistry.class);
    private BeanConfigurationsBeansCollector beanConfigurationsBeansCollector = new BeanConfigurationsBeansCollector(beanRegistry);

    @Mock
    private BeanConfiguration beanConfiguration;
    @Mock
    private BeanConfiguration anotherBeanConfiguration;
    @Mock
    private BeanConfiguration yetAnotherBeanConfiguration;

    @Test
    public void testCollectBeansLetsTheBeanConfigurationsDefineTheirBeans() {
        beanConfigurationsBeansCollector.collectBeans(Collections.singletonList(beanConfiguration));

        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testMultipleCallsToCollectBeansDontHandleOldBeanConfigurations() {
        beanConfigurationsBeansCollector.collectBeans(Collections.singletonList(beanConfiguration));
        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        beanConfigurationsBeansCollector.collectBeans(Collections.<BeanConfiguration>emptyList());

        verifyNoMoreInteractions(beanConfiguration);
    }

    @Test
    public void testDefineBeanRegistersBeanAtTheBeanRegistry() {
        beanConfigurationsBeansCollector.defineBean("bean", this);

        verify(beanRegistry).registerBean("bean", this);
    }

    @Test
    public void testLookUpBeanByNameAndTypeDelegatesToTheBeanRegistry() {
        when(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class)).thenReturn(this);

        BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class);

        assertThat(bean, is(this));
    }

    @Test
    public void testLookUpBeanByNameAndTypeHandlesFurtherBeanConfigurationsUntilTheBeanIsFound() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                when(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class))
                        .thenReturn(BeanConfigurationsBeansCollectorTest.this);
                return null;
            }
        }).when(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class);
                assertThat(bean, is(BeanConfigurationsBeansCollectorTest.this));
                return null;
            }
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testLookUpBeanByTypeDelegatesToTheBeanRegistry() {
        when(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest.class)).thenReturn(this);

        BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest.class);

        assertThat(bean, is(this));
    }

    @Test
    public void testLookUpBeanByTypeHandlesFurtherBeanConfigurationsUntilTheBeanIsFound() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                when(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest.class))
                        .thenReturn(BeanConfigurationsBeansCollectorTest.this);
                return null;
            }
        }).when(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest.class);
                assertThat(bean, is(BeanConfigurationsBeansCollectorTest.this));
                return null;
            }
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testLookUpBeansByTypeDelegatesToTheBeanRegistry() {
        when(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest.class)).thenReturn(Collections.singletonList(this));

        List<BeanConfigurationsBeansCollectorTest> beans = beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest.class);

        assertThat(beans.size(), is(1));
        assertThat(beans.get(0), is(this));
    }

    @Test
    public void testLookUpBeansByTypeCollectsRemainingBeansFirst() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
                return null;
            }
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration));

        InOrder inOrder = inOrder(anotherBeanConfiguration, beanRegistry);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistry).lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
    }
}