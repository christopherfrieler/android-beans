package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.mockito.Mockito
import rocks.frieler.android.beans.BeanConfiguration.Readiness

class BeanConfigurationsBeansCollectorTest {
	private val beanRegistry : BeanRegistry = mock()
	private val beanConfigurationsBeansCollector = BeanConfigurationsBeansCollector(beanRegistry)

	private val beanConfiguration: BeanConfiguration = mock()
	private val anotherBeanConfiguration: BeanConfiguration = mock()
	private val yetAnotherBeanConfiguration: BeanConfiguration = mock()

	@Test
	fun `collectBeans() let's the BeanConfigurations define their beans`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
	}

	@Test
	fun `collectBeans() resolves dependencies between BeanConfigurations and handles them in a possible order`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		doAnswer {
			whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		}.whenever(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		whenever(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		doAnswer {
			whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		}.whenever(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration))

		val inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration)
		inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)
	}

	@Test
	fun `collectBeans() delays BeanConfiguration waiting for an optional dependency`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.DELAY)
		whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		doAnswer {
			Mockito.`when`(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		}.whenever(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		whenever(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration))

		val inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration)
		inOrder.verify(yetAnotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		inOrder.verify(anotherBeanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
	}

	@Test
	fun `multiple calls to collectBeans() don't handle old BeanConfigurations`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		reset(beanConfiguration)

		beanConfigurationsBeansCollector.collectBeans(emptyList())

		verifyNoMoreInteractions(beanConfiguration)
	}

	@Test
	fun `collectBeans() applies the BeanRegistryPostProcessor beans after collecting all beans`() {
		val beanRegistryPostProcessor : BeanRegistryPostProcessor = mock()
		whenever(beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class.java)).thenReturn(listOf(beanRegistryPostProcessor))
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanConfiguration, beanRegistryPostProcessor)
		inOrder.verify(beanConfiguration).defineBeans(beanConfigurationsBeansCollector)
		inOrder.verify(beanRegistryPostProcessor).postProcess(beanRegistry)
	}

	@Test
	fun `defineBean() registers a singleton bean at the BeanRegistry`() {
		beanConfigurationsBeansCollector.defineBean(this)

		verify(beanRegistry).registerBean(this)
	}

	@Test
	fun `defineBean() with explicit name registers a singleton bean at the BeanRegistry`() {
		beanConfigurationsBeansCollector.defineBean("bean", this)

		verify(beanRegistry).registerBean("bean", this)
	}

	@Test
	fun `registerBeanPostProcessor() registers the BeanPostProcessor at the BeanRegistry`() {
		val beanPostProcessor : BeanPostProcessor = mock()

		beanConfigurationsBeansCollector.registerBeanPostProcessor(beanPostProcessor)

		verify(beanRegistry).registerBeanPostProcessor(beanPostProcessor)
	}

	@Test
	fun `lookUpBean() by name and type delegates to the BeanRegistry`() {
		whenever(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class.java)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class.java)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by name and type delegates to the BeanRegistry and returns null without such bean`() {
		whenever(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class.java)).thenReturn(null)

		val bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class.java)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpBean() by type delegates to the BeanRegistry to return the bean`() {
		whenever(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest::class.java)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest::class.java)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by type delegates to the BeanRegistry and returns null without such bean`() {
		whenever(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest::class.java)).thenReturn(null)

		val bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest::class.java)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpBeans() by type delegates to the BeanRegistry`() {
		whenever(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest::class.java)).thenReturn(listOf(this))

		val beans = beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest::class.java)

		assertThat(beans).hasSize(1)
		assertThat(beans).contains(this)
	}
}
