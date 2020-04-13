package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeanConfiguration.Readiness
import rocks.frieler.android.beans.BeanDependency.Fulfillment

@RunWith(MockitoJUnitRunner::class)
class BeanConfigurationTest {
	private val beansProvider: BeansProvider = mock()

	@Test
	fun `BeanConfiguration has no BeanDependencies by default`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		assertThat(beanConfiguration.dependencies).isEmpty()
	}

	@Test
	fun `BeanConfiguration depends on bean required by name and type`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		val dependency = beanConfiguration.requireBean("name", BeanConfigurationTest::class)

		assertThat(beanConfiguration.dependencies).contains(dependency)
	}

	@Test
	fun `BeanConfiguration depends on bean required by type`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		val dependency = beanConfiguration.requireBean(type = BeanConfigurationTest::class)

		assertThat(beanConfiguration.dependencies).contains(dependency)
	}

	@Test
	fun `BeanConfiguration depends on optional bean required by type`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		val dependency = beanConfiguration.requireOptionalBean(type = BeanConfigurationTest::class)

		assertThat(beanConfiguration.dependencies).contains(dependency)
	}

	@Test
	fun `BeanConfiguration depends on beans required by type`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		val dependency = beanConfiguration.requireBeans(BeanConfigurationTest::class)

		assertThat(beanConfiguration.dependencies).contains(dependency)
	}

	@Test
	fun `BeanConfiguration without dependencies is ready to define its beans`() {
		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies()

		assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider)).isEqualTo(Readiness.READY)
	}

	@Test
	fun `BeanConfiguration is ready toD define its beans when all dependencies are fulfilled`() {
		val dependency1 : BeanDependency<*> = mock()
		whenever(dependency1.fulfill(beansProvider)).thenReturn(Fulfillment.FULFILLED)
		val dependency2 : BeanDependency<*> = mock()
		whenever(dependency2.fulfill(beansProvider)).thenReturn(Fulfillment.FULFILLED)

		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies(dependency1, dependency2)

		assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider)).isEqualTo(Readiness.READY)
	}

	@Test
	fun `BeanConfiguration asks to be delayed to define its beans when all dependencies are fulfilled or unfulfilled optional`() {
		val dependency1 : BeanDependency<*> = mock()
		whenever(dependency1.fulfill(beansProvider)).thenReturn(Fulfillment.FULFILLED)
		val dependency2 : BeanDependency<*> = mock()
		whenever(dependency2.fulfill(beansProvider)).thenReturn(Fulfillment.UNFULFILLED_OPTIONAL)

		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies(dependency1, dependency2)

		assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider)).isEqualTo(Readiness.DELAY)
	}

	@Test
	fun `BeanConfiguration is not ready to define its beans when one of its dependencies is unfulfilled`() {
		val dependency1 : BeanDependency<*> = mock()
		whenever(dependency1.fulfill(beansProvider)).thenReturn(Fulfillment.FULFILLED)
		val dependency2 : BeanDependency<*> = mock()
		whenever(dependency2.fulfill(beansProvider)).thenReturn(Fulfillment.UNFULFILLED_OPTIONAL)
		val dependency3 : BeanDependency<*> = mock()
		whenever(dependency3.fulfill(beansProvider)).thenReturn(Fulfillment.UNFULFILLED)

		val beanConfiguration: BeanConfiguration = BeanConfigurationWithDefinedDependencies(dependency1, dependency2, dependency3)

		assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider)).isEqualTo(Readiness.UNREADY)
	}

	private class BeanConfigurationWithDefinedDependencies(vararg beanDependencies: BeanDependency<*>) : BeanConfiguration() {
		init {
			beanDependencies.forEach { dependency ->
				addDependency(dependency)
			}
		}

		override fun getBeanDefinitions(): List<BeanDefinition<*>> {
			return emptyList()
		}
	}
}
