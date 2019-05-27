package rocks.frieler.android.beans;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.FULFILLED;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED;
import static rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED_OPTIONAL;

public class BeanDependencyFulfillmentTest {
    @Test
    public void testUnfulfilledIsLowerThanUnfulfilledOptional() {
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED, UNFULFILLED_OPTIONAL), is(UNFULFILLED));
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED_OPTIONAL, UNFULFILLED), is(UNFULFILLED));
    }

    @Test
    public void testUnfulfilledIsLowerThanFulfilled() {
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED, FULFILLED), is(UNFULFILLED));
        assertThat(BeanDependency.Fulfillment.min(FULFILLED, UNFULFILLED), is(UNFULFILLED));
    }

    @Test
    public void testUnfulfilledOptionalIsLowerThanFulfilled() {
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED_OPTIONAL, FULFILLED), is(UNFULFILLED_OPTIONAL));
        assertThat(BeanDependency.Fulfillment.min(FULFILLED, UNFULFILLED_OPTIONAL), is(UNFULFILLED_OPTIONAL));
    }

    @Test
    public void testMinOfSameFulfillmentIsThatFulfillment() {
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED, UNFULFILLED), is(UNFULFILLED));
        assertThat(BeanDependency.Fulfillment.min(UNFULFILLED_OPTIONAL, UNFULFILLED_OPTIONAL), is(UNFULFILLED_OPTIONAL));
        assertThat(BeanDependency.Fulfillment.min(FULFILLED, FULFILLED), is(FULFILLED));
    }
}
