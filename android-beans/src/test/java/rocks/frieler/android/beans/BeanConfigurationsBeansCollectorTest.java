package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
    public void testCollectBeansResolvesDependenciesBetweenBeanConfigurationsAndHandlesThemInAPossibleOrder() {
        final BeanDependency<?> dependencyOnBean1 = mock(BeanDependency.class);
        when(dependencyOnBean1.fulfill(beanConfigurationsBeansCollector)).thenReturn(false);
        final BeanDependency<?> dependencyOnBean2 = mock(BeanDependency.class);
        when(dependencyOnBean2.fulfill(beanConfigurationsBeansCollector)).thenReturn(false);

        when(beanConfiguration.getDependencies()).thenReturn(Collections.singletonList(dependencyOnBean1));
        doAnswer(invocation -> {
            assertThat(dependencyOnBean1.get(), is(notNullValue()));
            final Object bean2 = new Object();
            beanConfigurationsBeansCollector.defineBean("bean2", bean2);
            when(dependencyOnBean2.fulfill(beanConfigurationsBeansCollector)).thenAnswer(fulfillInvocation -> {
                doReturn(bean2).when(dependencyOnBean2).get();
                return true;
            });
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        when(anotherBeanConfiguration.getDependencies()).thenReturn(Collections.singletonList(dependencyOnBean2));
        doAnswer(invocation -> {
            assertThat(dependencyOnBean2.get(), is(notNullValue()));
            return null;
        }).when(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        when(yetAnotherBeanConfiguration.getDependencies()).thenReturn(Collections.emptyList());
        doAnswer(invocation -> {
            final Object bean1 = new Object();
            beanConfigurationsBeansCollector.defineBean("bean1", bean1);
            when(dependencyOnBean1.fulfill(beanConfigurationsBeansCollector)).thenAnswer(fulfillInvocation -> {
                doReturn(bean1).when(dependencyOnBean1).get();
                return true;
            });
            return null;
        }).when(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testMultipleCallsToCollectBeansDontHandleOldBeanConfigurations() {
        beanConfigurationsBeansCollector.collectBeans(Collections.singletonList(beanConfiguration));
        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        reset(beanConfiguration);

        beanConfigurationsBeansCollector.collectBeans(Collections.emptyList());

        verifyNoMoreInteractions(beanConfiguration);
    }

    @Test
    public void testCollectBeansAppliesTheBeanRegistryPostProcessorBeansAfterCollectingAllBeans() {
        final BeanRegistryPostProcessor beanRegistryPostProcessor = mock(BeanRegistryPostProcessor.class);
        when(beanRegistry.lookUpBeans(BeanRegistryPostProcessor.class)).thenReturn(Collections.singletonList(beanRegistryPostProcessor));

        beanConfigurationsBeansCollector.collectBeans(Collections.singletonList(beanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, beanRegistryPostProcessor);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistryPostProcessor).postProcess(beanRegistry);
    }

    @Test
    public void testDefineBeanRegistersASingletonBeanAtTheBeanRegistry() {
        beanConfigurationsBeansCollector.defineBean(this);

        verify(beanRegistry).registerBean(this);
    }

    @Test
    public void testDefineBeanWithExplicitNameRegistersASingletonBeanAtTheBeanRegistry() {
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
    public void testLookUpBeanByNameAndTypeDelegatesToTheBeanRegistryAndReturnsNullWithoutSuchABean() {
        when(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class)).thenReturn(null);

        BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanByTypeDelegatesToTheBeanRegistryToReturnTheBean() {
        when(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest.class)).thenReturn(this);

        BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest.class);

        assertThat(bean, is(this));
    }

    @Test
    public void testLookUpBeanByTypeDelegatesToTheBeanRegistryAndReturnsNullWithoutSuchABean() {
        when(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest.class)).thenReturn(null);

        BeanConfigurationsBeansCollectorTest bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest.class);

        assertThat(bean, is(nullValue()));
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
        doAnswer(invocation -> {
            beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration));

        InOrder inOrder = inOrder(anotherBeanConfiguration, beanRegistry);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistry).lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
    }
}