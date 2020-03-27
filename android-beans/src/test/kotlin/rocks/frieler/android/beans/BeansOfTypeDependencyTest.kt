package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
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

	@Test
	fun `get() obtains beans from BeansProvider`() {
		whenever(beansProvider.lookUpBeans(BeansOfTypeDependencyTest::class)).thenReturn(listOf(this))

		beansOfTypeDependency.fulfill(beansProvider)
		val beans = beansOfTypeDependency.get()

		assertThat(beans).isEqualTo(beansProvider.lookUpBeans(BeansOfTypeDependencyTest::class))
	}

	@Test
	fun `get() fails when not yet fulfilled`() {
		assertThat {
			beansOfTypeDependency.get()

		}.isFailure()
				.hasClass(IllegalStateException::class)
	}
}
