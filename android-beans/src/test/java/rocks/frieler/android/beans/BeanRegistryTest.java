package rocks.frieler.android.beans;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import rocks.frieler.android.beans.scopes.ScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean;
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.prototype;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazy;

@RunWith(MockitoJUnitRunner.class)
public class BeanRegistryTest {
    private BeanRegistry beanRegistry = new BeanRegistry();

    @Test
    public void testLookUpBeanReturnsNullWithoutABeanWithTheDesiredName() {
        Object bean = beanRegistry.lookUpBean("bean", Object.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsNullWhenTheBeanWithTheDesiredNameIsOfAnIncompatibleType() {
        String name = "bean";
        Object registeredBean = new Object();

        beanRegistry.registerBean(name, registeredBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsSingletonBeanWithTheDesiredNameAndType() {
        String name = "bean";
        Object registeredBean = new Object();

        beanRegistry.registerBean(name, registeredBean);
        Object bean = beanRegistry.lookUpBean(name, Object.class);

        assertThat(bean, is(registeredBean));
    }

    @Test
    public void testLookUpBeanByTypeReturnsNullWithoutMatchingBean() {
        beanRegistry.registerBean("bean", new Object());
        BeanRegistryTest bean = beanRegistry.lookUpBean(BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanByTypeReturnsSingleMatchingBean() {
        Object registeredBean = new Object();

        beanRegistry.registerBean("bean", registeredBean);
        Object bean = beanRegistry.lookUpBean(Object.class);

        assertThat(bean, is(registeredBean));
    }

    @Test
    public void testLookUpBeanByTypeReturnsFirstMatchingBean() {
        beanRegistry.registerBean("bean1", new Object());
        beanRegistry.registerBean("bean2", new Object());
        Object bean = beanRegistry.lookUpBean(Object.class);

        assertThat(bean, is(notNullValue()));
    }

    @Test
    public void testLookUpBeansByTypeReturnsAllBeansAssignableToTheDesiredType() {
        beanRegistry.registerBean("object", new Object());
        beanRegistry.registerBean("long", 42L);
        beanRegistry.registerBean("double", 3.14);

        List<Number> numbers = beanRegistry.lookUpBeans(Number.class);

        assertThat(numbers.size(), is(2));
        assertThat(numbers.contains(42L), is(true));
        assertThat(numbers.contains(3.14), is(true));
    }

    @Test
    public void testRegisteredBeanWithoutExplicitNameIsRegisteredWithGeneratedName() {
        Object bean = new Object();

        beanRegistry.registerBean(bean);

        assertThat(beanRegistry.lookUpBean(Object.class.getName(), Object.class), is(sameInstance(bean)));
    }

    @Test
    public void testGeneratedBeanNameUsesAscendingNumbersToInCaseOfCollisions() {
        Object bean1 = new Object();
        Object bean2 = new Object();

        beanRegistry.registerBean(bean1);
        beanRegistry.registerBean(bean2);

        assertThat(beanRegistry.lookUpBean(Object.class.getName(), Object.class), is(sameInstance(bean1)));
        assertThat(beanRegistry.lookUpBean(Object.class.getName() + "2", Object.class), is(sameInstance(bean2)));
    }

    /* tests for bean-scopes: */
    @Mock private ScopedFactoryBeanHandler beanScope;

    @Before
    public void setupBeanScope() {
        when(beanScope.getName()).thenReturn("aScope");
        beanRegistry.addBeanScope(beanScope);
    }

    @Test
    public void testLookUpBeanReturnsNullWhenScopedFactoryBeanWithDesiredNameProducesWrongType() {
        final String name = "bean";
        @SuppressWarnings("unchecked") ScopedFactoryBean<Object> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(Object.class);

        beanRegistry.registerBean(name, factoryBean);
        Object bean = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsNullWithoutSuitableScopeForScopedFactoryBean() {
        final String name = "bean";
        @SuppressWarnings("unchecked") ScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(BeanRegistryTest.class);
        when(factoryBean.getScope()).thenReturn("otherScope");

        beanRegistry.registerBean(name, factoryBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(name, factoryBean.getBeanType());

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsNullDespiteScopedFactoryBeanWithDesiredNameWhenScopeIsNotActive() {
        when(beanScope.isActive()).thenReturn(false);
        final String name = "bean";
        final String scope = beanScope.getName();
        @SuppressWarnings("unchecked") ScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(BeanRegistryTest.class);
        when(factoryBean.getScope()).thenReturn(scope);

        beanRegistry.registerBean(name, factoryBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(name, factoryBean.getBeanType());

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsScopedBeanFromActiveScopeWithTheDesiredNameAndType() {
        when(beanScope.isActive()).thenReturn(true);
        final String name = "bean";
        final String scope = beanScope.getName();
        final Object scopedBean = new Object();
        @SuppressWarnings("unchecked") ScopedFactoryBean<Object> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(Object.class);
        when(factoryBean.getScope()).thenReturn(scope);
        when(factoryBean.produceBean()).thenReturn(scopedBean);
        doAnswer(invocation -> ((ScopedFactoryBean) invocation.getArgument(1)).produceBean())
                .when(beanScope).getBean(eq(name), any(ScopedFactoryBean.class));

        beanRegistry.registerBean(name, factoryBean);
        Object bean = beanRegistry.lookUpBean(name, factoryBean.getBeanType());

        assertThat(bean, is(sameInstance(scopedBean)));
    }

    @Test
    public void testLookUpBeanByTypeReturnsMatchingBeanFromScopedFactoryBean() {
        when(beanScope.isActive()).thenReturn(true);
        final String name = "bean";
        final String scope = beanScope.getName();
        @SuppressWarnings("unchecked") ScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(BeanRegistryTest.class);
        when(factoryBean.getScope()).thenReturn(scope);
        when(factoryBean.produceBean()).thenReturn(this);
        doAnswer(invocation -> ((ScopedFactoryBean) invocation.getArgument(1)).produceBean())
                .when(beanScope).getBean(eq(name), any(ScopedFactoryBean.class));

        beanRegistry.registerBean(name, factoryBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(factoryBean.getBeanType());

        assertThat(bean, is(this));
    }

    @Test
    public void testLookUpBeansByTypeReturnsAllBeansAssignableToTheDesiredTypeIncludingScopedBeans() {
        when(beanScope.isActive()).thenReturn(true);
        beanRegistry.registerBean("object", new Object());
        beanRegistry.registerBean("long", 42L);
        final String name = "double";
        final String scope = beanScope.getName();
        @SuppressWarnings("unchecked") ScopedFactoryBean<Double> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(Double.class);
        when(factoryBean.getScope()).thenReturn(scope);
        when(factoryBean.produceBean()).thenReturn(3.14);
        doAnswer(invocation -> ((ScopedFactoryBean) invocation.getArgument(1)).produceBean())
                .when(beanScope).getBean(eq(name), any(ScopedFactoryBean.class));
        beanRegistry.registerBean(name, factoryBean);

        List<Number> numbers = beanRegistry.lookUpBeans(Number.class);

        assertThat(numbers.size(), is(2));
        assertThat(numbers.contains(42L), is(true));
        assertThat(numbers.contains(3.14), is(true));
    }

    @Test
    public void testGeneratedBeanNameForScopedFactoryBeansIsDerivedFromProducedBeanType() {
        final ScopedFactoryBeanHandler scope = mock(ScopedFactoryBeanHandler.class);
        when(scope.getName()).thenReturn("scope");
        when(scope.isActive()).thenReturn(true);
        beanRegistry.addBeanScope(scope);

        @SuppressWarnings("unchecked") ScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getScope()).thenReturn("scope");
        when(factoryBean.getBeanType()).thenReturn(BeanRegistryTest.class);
        //noinspection unchecked
        when(scope.<BeanRegistryTest>getBean(eq(BeanRegistryTest.class.getName()), any(ScopedFactoryBean.class))).thenReturn(this);

        beanRegistry.registerBean(factoryBean);

        assertThat(beanRegistry.lookUpBean(this.getClass().getName(), this.getClass()), is(sameInstance(this)));
    }

    @Test
    public void testBeanRegistrySupportsLazyInstantiationInSingletonScope() {
        final String name = "lazyInstantiatedBean";
        final SingletonScopedFactoryBean<BeanRegistryTest> singletonFactory = lazy(BeanRegistryTest.class, () -> this);

        beanRegistry.registerBean(name, singletonFactory);
        final BeanRegistryTest beanInstance = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(beanInstance, is(singletonFactory.produceBean()));
    }

    @Test
    public void testBeanRegistrySupportsPrototypeScope() {
        final String name = "prototypeBean";
        final PrototypeScopedFactoryBean<BeanRegistryTest> prototype = prototype(BeanRegistryTest.class, () -> this);

        beanRegistry.registerBean(name, prototype);
        final BeanRegistryTest beanInstance = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(beanInstance, is(prototype.produceBean()));
    }

    /* tests for post-processing: */
    @Mock private BeanPostProcessor beanPostProcessor;

    @Test
    public void testRegisteredBeanPostProcessorGetsNewBeansToPostProcess() {
        Object originalBean = new Object();
        Object replacementBean = new Object();
        when(beanPostProcessor.postProcessBean("bean", originalBean)).thenReturn(replacementBean);

        beanRegistry.registerBeanPostProcessor(beanPostProcessor);
        beanRegistry.registerBean("bean", originalBean);

        verify(beanPostProcessor).postProcessBean("bean", originalBean);
        assertThat(beanRegistry.lookUpBean("bean", Object.class), is(replacementBean));
    }

    @Test
    public void testRegisteredBeanPostProcessorGetsExistingBeansToPostProcess() {
        Object originalBean = new Object();
        Object replacementBean = new Object();
        when(beanPostProcessor.postProcessBean("bean", originalBean)).thenReturn(replacementBean);

        beanRegistry.registerBean("bean", originalBean);
        beanRegistry.registerBeanPostProcessor(beanPostProcessor);

        verify(beanPostProcessor).postProcessBean("bean", originalBean);
        assertThat(beanRegistry.lookUpBean("bean", Object.class), is(replacementBean));
    }

    @Test
    public void testRegisteredBeanPostProcessorGetsScopedBeanToPostProcessWhenItGetsProduced() {
        when(beanScope.isActive()).thenReturn(true);
        final String name = "bean";
        final String scope = beanScope.getName();
        final Object originalBean = new Object();
        @SuppressWarnings("unchecked") ScopedFactoryBean<Object> factoryBean = mock(ScopedFactoryBean.class);
        when(factoryBean.getBeanType()).thenReturn(Object.class);
        when(factoryBean.getScope()).thenReturn(scope);
        when(factoryBean.produceBean()).thenReturn(originalBean);
        doAnswer(invocation -> ((ScopedFactoryBean) invocation.getArgument(1)).produceBean())
                .when(beanScope).getBean(eq(name), any(ScopedFactoryBean.class));

        when(beanPostProcessor.postProcessBean(name, factoryBean)).thenReturn(factoryBean);
        Object replacementBean = new Object();
        when(beanPostProcessor.postProcessBean(name, originalBean)).thenReturn(replacementBean);

        beanRegistry.registerBean(name, factoryBean);
        beanRegistry.registerBeanPostProcessor(beanPostProcessor);

        assertThat(beanRegistry.lookUpBean(name, Object.class), is(replacementBean));
        verify(beanPostProcessor).postProcessBean(name, originalBean);
    }
}