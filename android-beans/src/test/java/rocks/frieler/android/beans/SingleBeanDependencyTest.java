package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import rocks.frieler.android.beans.BeanDependency.Fulfillment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.FULFILLED;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED;

@RunWith(MockitoJUnitRunner.class)
public class SingleBeanDependencyTest {
    @Mock
    private BeansProvider beansProvider;
    @Mock
    private SingleBeanDependencyTest bean;

    @Test
    public void testFulfillReturnsFalseWhenTheDesiredBeanIsNotAvailable() {
        SingleBeanDependency<SingleBeanDependencyTest> beanDependency = new SingleBeanDependency<>("bean", SingleBeanDependencyTest.class);
        Fulfillment fulfillment = beanDependency.fulfill(beansProvider);

        assertThat(fulfillment, is(UNFULFILLED));
    }

    @Test
    public void testFulfillReturnsTrueWhenTheDesiredBeanByTypeIsAvailable() {
        when(beansProvider.lookUpBean(SingleBeanDependencyTest.class)).thenReturn(bean);

        SingleBeanDependency<SingleBeanDependencyTest> beanDependency = new SingleBeanDependency<>(SingleBeanDependencyTest.class);
        Fulfillment fulfillment = beanDependency.fulfill(beansProvider);

        assertThat(fulfillment, is(FULFILLED));
    }

    @Test
    public void testFulfillReturnsTrueWhenTheDesiredBeanByNameAndTypeIsAvailable() {
        when(beansProvider.lookUpBean("bean", SingleBeanDependencyTest.class)).thenReturn(bean);

        SingleBeanDependency<SingleBeanDependencyTest> beanDependency = new SingleBeanDependency<>("bean", SingleBeanDependencyTest.class);
        Fulfillment fulfillment = beanDependency.fulfill(beansProvider);

        assertThat(fulfillment, is(FULFILLED));
    }

    @Test
    public void testFulfillReturnsTrueWithoutQueryingTheBeansProviderAfterItWasFulfilledAlready() {
        when(beansProvider.lookUpBean("bean", SingleBeanDependencyTest.class)).thenReturn(bean);

        SingleBeanDependency<SingleBeanDependencyTest> beanDependency = new SingleBeanDependency<>("bean", SingleBeanDependencyTest.class);
        beanDependency.fulfill(beansProvider);
        reset(beansProvider);

        Fulfillment fulfillment = beanDependency.fulfill(beansProvider);

        verifyZeroInteractions(beansProvider);
        assertThat(fulfillment, is(FULFILLED));
    }

    @Test
    public void testGetReturnsBeanAfterTheBeanDependencyWasFulfilled() {
        when(beansProvider.lookUpBean("bean", SingleBeanDependencyTest.class)).thenReturn(bean);

        SingleBeanDependency<SingleBeanDependencyTest> beanDependency = new SingleBeanDependency<>("bean", SingleBeanDependencyTest.class);
        beanDependency.fulfill(beansProvider);
        SingleBeanDependencyTest dependency = beanDependency.get();

        assertThat(dependency, is(bean));
    }
}