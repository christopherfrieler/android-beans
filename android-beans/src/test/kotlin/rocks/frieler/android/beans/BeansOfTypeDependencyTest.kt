package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeanDependency.Fulfillment

@RunWith(MockitoJUnitRunner::class)
class BeansOfTypeDependencyTest {
	private val beansOfTypeDependency = BeansOfTypeDependency(BeansOfTypeDependencyTest::class)

	private val beansProvider: BeansProvider = mock()

	@Test
	fun `fulfill() returns UNFULFILLED_OPTIONAL`() {
		val fulfillment = beansOfTypeDependency.fulfill(beansProvider)

		verifyZeroInteractions(beansProvider)
		assertThat(fulfillment).isEqualTo(Fulfillment.UNFULFILLED_OPTIONAL)
	}
}
