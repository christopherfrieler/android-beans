package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.Optional;
import rocks.frieler.android.beans.BeanDependency.Fulfillment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.FULFILLED;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED_OPTIONAL;

@RunWith(MockitoJUnitRunner.class)
public class OptionalSingleBeanDependencyTest {
    @Mock
    private BeansProvider beansProvider;

    @Test
    public void testOptionalSingleBeanDependencyIsUnfulfilledOptionalWhenTheNoSuitableBeanIsAvailable() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(null);

        Fulfillment fulfillment = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class).fulfill(beansProvider);

        assertThat(fulfillment, is(UNFULFILLED_OPTIONAL));
    }

    @Test
    public void testOptionalSingleBeanDependencyIsFulfilledWhenASuitableBeanIsAvailable() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        final Fulfillment fulfillment = optionalSingleBeanDependency.fulfill(beansProvider);

        assertThat(fulfillment, is(FULFILLED));
    }

    @Test
    public void testOptionalSingleBeanDependencyRemainsFulfilledWithoutQueryingTheBeansProviderAgainWhenItWasFulfilledEarlier() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        clearInvocations(beansProvider);

        final Fulfillment fulfillment = optionalSingleBeanDependency.fulfill(beansProvider);

        verifyZeroInteractions(beansProvider);
        assertThat(fulfillment, is(FULFILLED));
    }

    @Test
    public void testGetReturnsEmptyOptionalWhenTheBeanDependencyHasNeverBeanFulfilled() {
        final OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        final Optional<OptionalSingleBeanDependencyTest> optionalBean = optionalSingleBeanDependency.get();

        assertThat(optionalBean.isEmpty(), is(true));
    }

    @Test
    public void testGetReturnsEmptyOptionalWhenWantedBeanWasNotAvailable() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(null);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> optionalBean = optionalSingleBeanDependency.get();

        assertThat(optionalBean.isEmpty(), is(true));
    }

    @Test
    public void testGetReturnsBeanItWasFulfilledWithByType() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> optionalBean = optionalSingleBeanDependency.get();

        assertThat(optionalBean.isPresent(), is(true));
        assertThat(optionalBean.get(), is(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)));
    }

    @Test
    public void testGetReturnsBeanItWasFulfilledWithByNameAndType() {
        when(beansProvider.lookUpBean("bean", OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>("bean", OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> optionalBean = optionalSingleBeanDependency.get();

        assertThat(optionalBean.isPresent(), is(true));
        assertThat(optionalBean.get(), is(beansProvider.lookUpBean("bean", OptionalSingleBeanDependencyTest.class)));
    }
}
