package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeanDependency.Fulfillment

@RunWith(MockitoJUnitRunner::class)
class OptionalSingleBeanDependencyTest {
	private val beansProvider: BeansProvider = mock()

	@Test
	fun testOptionalSingleBeanDependencyIsUnfulfilledOptionalWhenTheNoSuitableBeanIsAvailable() {
		whenever(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest::class)).thenReturn(null)

		val optionalSingleBeanDependency = OptionalSingleBeanDependency(OptionalSingleBeanDependencyTest::class)
		val fulfillment = optionalSingleBeanDependency.fulfill(beansProvider)

		assertThat(fulfillment).isEqualTo(Fulfillment.UNFULFILLED_OPTIONAL)
	}

	@Test
	fun testOptionalSingleBeanDependencyIsFulfilledWhenASuitableBeanIsAvailable() {
		whenever(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest::class)).thenReturn(this)

		val optionalSingleBeanDependency = OptionalSingleBeanDependency(OptionalSingleBeanDependencyTest::class)
		val fulfillment = optionalSingleBeanDependency.fulfill(beansProvider)

		assertThat(fulfillment).isEqualTo(Fulfillment.FULFILLED)
	}

	@Test
	fun testOptionalSingleBeanDependencyRemainsFulfilledWithoutQueryingTheBeansProviderAgainWhenItWasFulfilledEarlier() {
		whenever(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest::class)).thenReturn(this)

		val optionalSingleBeanDependency = OptionalSingleBeanDependency(OptionalSingleBeanDependencyTest::class)
		optionalSingleBeanDependency.fulfill(beansProvider)
		clearInvocations(beansProvider)
		val fulfillment = optionalSingleBeanDependency.fulfill(beansProvider)

		verifyZeroInteractions(beansProvider)
		assertThat(fulfillment).isEqualTo(Fulfillment.FULFILLED)
	}
}
