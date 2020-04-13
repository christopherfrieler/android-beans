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

	private val beanDefinitionWithoutName: BeanDefinition<*> = mock()
	private val beanDefinitionWithName: BeanDefinition<*> = mock()

	@Test
	fun `collectBeans() let's the BeanConfigurations define their beans`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		whenever(beanConfiguration.getBeanDefinitions())
				.thenReturn(listOf(beanDefinitionWithoutName, beanDefinitionWithName))
		whenever(beanDefinitionWithoutName.getName()).thenReturn(null)
		whenever(beanDefinitionWithoutName.produceBean()).thenReturn(Any())
		whenever(beanDefinitionWithName.getName()).thenReturn("bean")
		whenever(beanDefinitionWithName.produceBean()).thenReturn(Any())

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanConfiguration).getBeanDefinitions()
		verify(beanRegistry).registerBean(beanDefinitionWithoutName.produceBean())
		verify(beanRegistry).registerBean(beanDefinitionWithName.getName()!!, beanDefinitionWithName.produceBean())
	}

	@Test
	fun `collectBeans() resolves dependencies between BeanConfigurations and handles them in a possible order`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		whenever(beanConfiguration.getBeanDefinitions()).thenAnswer {
			whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
			emptyList<BeanDefinition<*>>()
		}
		whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		whenever(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
		whenever(yetAnotherBeanConfiguration.getBeanDefinitions()).thenAnswer {
			whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
			emptyList<BeanDefinition<*>>()
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration))

		val inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration)
		inOrder.verify(yetAnotherBeanConfiguration).getBeanDefinitions()
		inOrder.verify(beanConfiguration).getBeanDefinitions()
		inOrder.verify(anotherBeanConfiguration).getBeanDefinitions()
	}

	@Test
	fun `collectBeans() delays BeanConfiguration waiting for an optional dependency`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.DELAY)
		whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.UNREADY)
		whenever(yetAnotherBeanConfiguration.getBeanDefinitions()).thenAnswer {
			whenever(anotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)
			emptyList<BeanDefinition<*>>()
		}
		whenever(yetAnotherBeanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration))

		val inOrder = inOrder(beanConfiguration, anotherBeanConfiguration, yetAnotherBeanConfiguration)
		inOrder.verify(yetAnotherBeanConfiguration).getBeanDefinitions()
		inOrder.verify(anotherBeanConfiguration).getBeanDefinitions()
		inOrder.verify(beanConfiguration).getBeanDefinitions()
	}

	@Test
	fun `multiple calls to collectBeans() don't handle old BeanConfigurations`() {
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanConfiguration).getBeanDefinitions()
		reset(beanConfiguration)

		beanConfigurationsBeansCollector.collectBeans(emptyList())

		verifyNoMoreInteractions(beanConfiguration)
	}

	@Test
	fun `collectBeans() applies the BeanRegistryPostProcessor beans after collecting all beans`() {
		val beanRegistryPostProcessor : BeanRegistryPostProcessor = mock()
		whenever(beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class)).thenReturn(listOf(beanRegistryPostProcessor))
		whenever(beanConfiguration.isReadyToDefineBeans(beanConfigurationsBeansCollector)).thenReturn(Readiness.READY)

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanConfiguration, beanRegistryPostProcessor)
		inOrder.verify(beanConfiguration).getBeanDefinitions()
		inOrder.verify(beanRegistryPostProcessor).postProcess(beanRegistry)
	}

	@Test
	fun `lookUpBean() by name and type delegates to the BeanRegistry`() {
		whenever(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by name and type delegates to the BeanRegistry and returns null without such bean`() {
		whenever(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)

		val bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpBean() by type delegates to the BeanRegistry to return the bean`() {
		whenever(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest::class)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by type delegates to the BeanRegistry and returns null without such bean`() {
		whenever(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)

		val bean = beanConfigurationsBeansCollector.lookUpBean(BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpBeans() by type delegates to the BeanRegistry`() {
		whenever(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest::class)).thenReturn(listOf(this))

		val beans = beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest::class)

		assertThat(beans).hasSize(1)
		assertThat(beans).contains(this)
	}
}
