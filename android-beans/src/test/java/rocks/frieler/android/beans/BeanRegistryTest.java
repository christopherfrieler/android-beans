package rocks.frieler.android.beans;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import androidx.fragment.app.FragmentActivity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanRegistryTest {
    private ForegroundActivityHolder foregroundActivityHolder = mock(ForegroundActivityHolder.class);
    private BeanRegistry beanRegistry = new BeanRegistry(foregroundActivityHolder);

    @Mock
    private FragmentActivity activity;

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
    public void testLookUpBeanReturnsNullDespiteActivityScopedFactoryBeanWithDesiredNameWhenNoFragmentActivityIsCurrentlyInForeground() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(mock(Activity.class));
        final String name = "bean";
        ActivityScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ActivityScopedFactoryBean.class);

        beanRegistry.registerBean(name, factoryBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsNullWhenActivityScopedFactoryBeanWithDesiredNameProducesWrongType() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);
        final String name = "bean";
        ActivityScopedFactoryBean<Object> factoryBean = mock(ActivityScopedFactoryBean.class);
        when(factoryBean.getType()).thenReturn(Object.class);

        beanRegistry.registerBean(name, factoryBean);
        Object bean = beanRegistry.lookUpBean(name, BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void testLookUpBeanReturnsActivityScopedBeanFromAnActivityScopedFactoryBeanWithTheDesiredNameAndType() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);
        final String name = "bean";
        final Object scopedBean = new Object();
        ActivityScopedFactoryBean<Object> factoryBean = mock(ActivityScopedFactoryBean.class);
        when(factoryBean.getType()).thenReturn(Object.class);
        when(factoryBean.getBean(name, activity)).thenReturn(scopedBean);

        beanRegistry.registerBean(name, factoryBean);
        Object bean = beanRegistry.lookUpBean(name, Object.class);

        assertThat(bean, is(sameInstance(scopedBean)));
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
    public void testLookUpBeanByTypeReturnsMatchingBeanFromActivityScopedFactoryBean() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);
        final String name = "bean";
        ActivityScopedFactoryBean<BeanRegistryTest> factoryBean = mock(ActivityScopedFactoryBean.class);
        when(factoryBean.getType()).thenReturn(BeanRegistryTest.class);
        when(factoryBean.getBean(name, activity)).thenReturn(this);

        beanRegistry.registerBean(name, factoryBean);
        BeanRegistryTest bean = beanRegistry.lookUpBean(BeanRegistryTest.class);

        assertThat(bean, is(this));
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
    public void testLookUpBeansByTypeReturnsAllBeansAssignableToTheDesiredTypeIncludingActivityScopedBeans() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);
        beanRegistry.registerBean("object", new Object());
        beanRegistry.registerBean("long", 42L);
        ActivityScopedFactoryBean<Double> factoryBean = mock(ActivityScopedFactoryBean.class);
        when(factoryBean.getType()).thenReturn(Double.class);
        when(factoryBean.getBean("double", activity)).thenReturn(3.14);
        beanRegistry.registerBean("double", factoryBean);

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
    public void testGeneratedBeanNameForActivityScopedFactoryBeansIsDerivedFromProducedBeanType() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);
        ActivityScopedFactoryBean factoryBean = mock(ActivityScopedFactoryBean.class);
        when(factoryBean.getType()).thenReturn(this.getClass());
        when(factoryBean.getBean(anyString(), eq(activity))).thenReturn(this);

        beanRegistry.registerBean(factoryBean);

        assertThat(beanRegistry.lookUpBean(this.getClass().getName(), this.getClass()), is(sameInstance(this)));
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
}