package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isLessThan
import org.junit.Test
import rocks.frieler.android.beans.BeanDependency.Fulfillment
import rocks.frieler.android.beans.BeanDependency.Fulfillment.Comparison
import rocks.frieler.android.beans.BeanDependency.Fulfillment.FULFILLED
import rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED
import rocks.frieler.android.beans.BeanDependency.Fulfillment.UNFULFILLED_OPTIONAL

class BeanDependencyFulfillmentComparisonTest {
	@Test
	fun `UNFULFILLED is lower than UNFULFILLED_OPTIONAL`() {
		assertThat(Comparison.compare(UNFULFILLED, UNFULFILLED_OPTIONAL)).isLessThan(0)
		assertThat(Comparison.compare(UNFULFILLED_OPTIONAL, UNFULFILLED)).isGreaterThan(0)
	}

	@Test
	fun `UNFULFILLED is lower than FULFILLED`() {
		assertThat(Comparison.compare(UNFULFILLED, FULFILLED)).isLessThan(0)
		assertThat(Comparison.compare(FULFILLED, UNFULFILLED)).isGreaterThan(0)
	}

	@Test
	fun `UNFULFILLED_OPTIONAL is lower than FULFILLED`() {
		assertThat(Comparison.compare(UNFULFILLED_OPTIONAL, FULFILLED)).isLessThan(0)
		assertThat(Comparison.compare(FULFILLED, UNFULFILLED_OPTIONAL)).isGreaterThan(0)
	}

	@Test
	fun `Fulfillment is equal to itself`() {
		assertThat(Comparison.compare(UNFULFILLED, UNFULFILLED)).isEqualTo(0)
		assertThat(Comparison.compare(UNFULFILLED_OPTIONAL, UNFULFILLED_OPTIONAL)).isEqualTo(0)
		assertThat(Comparison.compare(FULFILLED, FULFILLED)).isEqualTo(0)
	}

	@Test
	fun `MAXIMUM is greater than or equal to all other Fulfillments`() {
		for (fulfillment in Fulfillment.values()) {
			assertThat(Comparison.compare(Comparison.MAXIMUM, fulfillment)).isGreaterThanOrEqualTo(0)
		}
	}
}
