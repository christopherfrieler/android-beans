package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import rocks.frieler.android.beans.BeanDependency.Fulfillment.*
import rocks.frieler.android.beans.BeanDependency.Fulfillment.Comparison.min

class BeanDependencyFulfillmentTest {
	@Test
	fun `UNFULFILLED is lower than UNFULFILLED_OPTIONAL`() {
		assertThat(min(UNFULFILLED, UNFULFILLED_OPTIONAL)).isEqualTo(UNFULFILLED)
		assertThat(min(UNFULFILLED_OPTIONAL, UNFULFILLED)).isEqualTo(UNFULFILLED)
	}

	@Test
	fun `UNFULFILLED is lower than FULFILLED`() {
		assertThat(min(UNFULFILLED, FULFILLED)).isEqualTo(UNFULFILLED)
		assertThat(min(FULFILLED, UNFULFILLED)).isEqualTo(UNFULFILLED)
	}

	@Test
	fun `UNFULFILLED_OPTIONAL is lower than FULFILLED`() {
		assertThat(min(UNFULFILLED_OPTIONAL, FULFILLED)).isEqualTo(UNFULFILLED_OPTIONAL)
		assertThat(min(FULFILLED, UNFULFILLED_OPTIONAL)).isEqualTo(UNFULFILLED_OPTIONAL)
	}

	@Test
	fun `min() of same Fulfillment is that Fulfillment`() {
		assertThat(min(UNFULFILLED, UNFULFILLED)).isEqualTo(UNFULFILLED)
		assertThat(min(UNFULFILLED_OPTIONAL, UNFULFILLED_OPTIONAL)).isEqualTo(UNFULFILLED_OPTIONAL)
		assertThat(min(FULFILLED, FULFILLED)).isEqualTo(FULFILLED)
	}
}
