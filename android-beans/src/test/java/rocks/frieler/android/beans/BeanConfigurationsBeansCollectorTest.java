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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.BeanConfiguration.Readiness.DELAY;
import static rocks.frieler.android.beans.BeanConfiguration.Readiness.READY;
import static rocks.frieler.android.beans.BeanConfiguration.Readiness.UNREADY;

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
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);

        beanConfigurationsBeansCollector.collectBeans(Collections.singletonList(beanConfiguration));

        verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testCollectBeansResolvesDependenciesBetweenBeanConfigurationsAndHandlesThemInAPossibleOrder() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(UNREADY);
        doAnswer(invocation -> {
            when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(UNREADY);

        when(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        doAnswer(invocation -> {
            when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
            return null;
        }).when(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testCollectBeansDelaysBeanConfigurationWaitingForAnOptionalDependency() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(DELAY);
        when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(UNREADY);
        doAnswer(invocation -> {
            when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
            return null;
        }).when(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        when(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testMultipleCallsToCollectBeansDontHandleOldBeanConfigurations() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
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
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);

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
    public void testRegisterBeanPostProcessorRegistersTheBeanPostProcessorAtTheBeanRegistry() {
        BeanPostProcessor beanPostProcessor = mock(BeanPostProcessor.class);

        beanConfigurationsBeansCollector.registerBeanPostProcessor(beanPostProcessor);

        verify(beanRegistry).registerBeanPostProcessor(beanPostProcessor);
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
    public void testLookUpBeanByNameAndTypeCalledDirectlyWhenDefiningBeansCollectsBeansFromReadyBeanConfigurationsBeforeQueryingTheBeanRegistry() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        doAnswer(invocation -> {
            beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class);
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        when(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(DELAY);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration, beanRegistry);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistry).lookUpBean("bean", BeanConfigurationsBeansCollectorTest.class);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
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
    public void testLookUpBeanByTypeCalledDirectlyWhenDefiningBeansCollectsBeansFromReadyBeanConfigurationsBeforeQueryingTheBeanRegistry() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        doAnswer(invocation -> {
            beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest.class);
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        when(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(DELAY);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration, beanRegistry);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistry).lookUpBean(BeanConfigurationsBeansCollectorTest.class);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }

    @Test
    public void testLookUpBeansByTypeDelegatesToTheBeanRegistry() {
        when(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest.class)).thenReturn(Collections.singletonList(this));

        List<BeanConfigurationsBeansCollectorTest> beans = beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest.class);

        assertThat(beans.size(), is(1));
        assertThat(beans.get(0), is(this));
    }

    @Test
    public void testLookUpBeansByTypeCalledDirectlyWhenDefiningBeansCollectsBeansFromReadyBeanConfigurationsBeforeQueryingTheBeanRegistry() {
        when(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        doAnswer(invocation -> {
            beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
            return null;
        }).when(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        when(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(READY);
        when(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(DELAY);

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration));

        InOrder inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration, beanRegistry);
        inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
        inOrder.verify(beanRegistry).lookUpBeans(BeanConfigurationsBeansCollectorTest.class);
        inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector);
    }
}
