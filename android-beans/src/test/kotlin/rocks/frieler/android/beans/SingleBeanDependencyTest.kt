package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeanDependency.Fulfillment

@RunWith(MockitoJUnitRunner::class)
class SingleBeanDependencyTest {
	private val beansProvider: BeansProvider = mock()
	private val bean: SingleBeanDependencyTest = mock()

	@Test
	fun `fulfill() returns UNFULFILLED when the desired bean is not available`() {
		val beanDependency = SingleBeanDependency("bean", SingleBeanDependencyTest::class)
		val fulfillment = beanDependency.fulfill(beansProvider)

		assertThat(fulfillment).isEqualTo(Fulfillment.UNFULFILLED)
	}

	@Test
	fun `fulfill() returns FULLFILLED when the desired bean by type is available`() {
		whenever(beansProvider.lookUpBean(SingleBeanDependencyTest::class)).thenReturn(bean)

		val beanDependency = SingleBeanDependency(SingleBeanDependencyTest::class)
		val fulfillment = beanDependency.fulfill(beansProvider)

		assertThat(fulfillment).isEqualTo(Fulfillment.FULFILLED)
	}

	@Test
	fun `fulfill() returns FULFILLED when the desired bean by name and type is available`() {
		whenever(beansProvider.lookUpBean("bean", SingleBeanDependencyTest::class)).thenReturn(bean)

		val beanDependency = SingleBeanDependency("bean", SingleBeanDependencyTest::class)
		val fulfillment = beanDependency.fulfill(beansProvider)

		assertThat(fulfillment).isEqualTo(Fulfillment.FULFILLED)
	}

	@Test
	fun `fulfill() returns FULFILLED without querying the BeansProvider after it was fulfilled already`() {
		whenever(beansProvider.lookUpBean("bean", SingleBeanDependencyTest::class)).thenReturn(bean)

		val beanDependency = SingleBeanDependency("bean", SingleBeanDependencyTest::class)
		beanDependency.fulfill(beansProvider)
		reset(beansProvider)
		val fulfillment = beanDependency.fulfill(beansProvider)

		verifyZeroInteractions(beansProvider)
		assertThat(fulfillment).isEqualTo(Fulfillment.FULFILLED)
	}
}
