package rocks.frieler.android.beans;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
    public void testLookUpBeanReturnsBeanWithTheDesiredNameAndType() {
        String name = "bean";
        Object registeredBean = new Object();

        beanRegistry.registerBean(name, registeredBean);
        Object bean = beanRegistry.lookUpBean(name, Object.class);

        assertThat(bean, is(registeredBean));
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
    public void testLookUpBeanByTypeReturnsNullWithoutMatchingBean() {
        beanRegistry.registerBean("bean", new Object());
        BeanRegistryTest bean = beanRegistry.lookUpBean(BeanRegistryTest.class);

        assertThat(bean, is(nullValue()));
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
}