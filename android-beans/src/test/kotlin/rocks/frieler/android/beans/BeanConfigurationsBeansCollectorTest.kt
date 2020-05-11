package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class BeanConfigurationsBeansCollectorTest {
	private val beanRegistry: BeanRegistry = mock()
	private val beanConfigurationsBeansCollector = BeanConfigurationsBeansCollector(beanRegistry)

	private val beanConfiguration: BeanConfiguration = mock()
	private val anotherBeanConfiguration: BeanConfiguration = mock()
	private val yetAnotherBeanConfiguration: BeanConfiguration = mock()

	private val beanDefinition = mock<BeanDefinition<*>>()
	private val anotherBeanDefinition = mock<BeanDefinition<*>>()
	private val yetAnotherBeanDefinition = mock<BeanDefinition<*>>()

	@Test
	fun `collectBeans() collects and processes the BeanConfigurations' BeanDefinitions providing itself as dependency-provider`() {
		val bean = Any()
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(bean)
		val anotherBean = Any()
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(anotherBean)
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))
		val yetAnotherBean = Any()
		whenever(yetAnotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(yetAnotherBean)
		whenever(anotherBeanConfiguration.getBeanDefinitions()).thenReturn(listOf(yetAnotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration))

		verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		verify(beanRegistry).registerBean(bean)
		verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
		verify(beanRegistry).registerBean(anotherBean)
		verify(yetAnotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
		verify(beanRegistry).registerBean(yetAnotherBean)
	}

	@Test
	fun `collectBeans() can process BeanDefinitions with and without name`() {
		val beanDefinitionWithoutName: BeanDefinition<*> = mock()
		val unnamedBean = Any()
		whenever(beanDefinitionWithoutName.getName()).thenReturn(null)
		whenever(beanDefinitionWithoutName.produceBean(beanConfigurationsBeansCollector)).thenReturn(unnamedBean)
		val beanDefinitionWithName: BeanDefinition<*> = mock()
		val namedBean = Any()
		whenever(beanDefinitionWithName.getName()).thenReturn("bean")
		whenever(beanDefinitionWithName.produceBean(beanConfigurationsBeansCollector)).thenReturn(namedBean)
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinitionWithoutName, beanDefinitionWithName))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanDefinitionWithoutName).produceBean(beanConfigurationsBeansCollector)
		verify(beanRegistry).registerBean(unnamedBean)
		verify(beanDefinitionWithName).produceBean(beanConfigurationsBeansCollector)
		verify(beanRegistry).registerBean("bean", namedBean)
	}

	@Test
	fun `multiple calls to collectBeans() don't handle old BeanConfigurations nor their BeanDefinitions`() {
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition))
		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanConfiguration).getBeanDefinitions()
		verify(beanDefinition).produceBean(any())
		reset(beanConfiguration, beanDefinition)

		beanConfigurationsBeansCollector.collectBeans(emptyList())

		verifyNoMoreInteractions(beanConfiguration)
		verifyNoMoreInteractions(beanDefinition)
	}

	@Test
	fun `collectBeans() applies the BeanRegistryPostProcessor beans after collecting all beans`() {
		val beanRegistryPostProcessor: BeanRegistryPostProcessor = mock()
		whenever(beanRegistry.lookUpBeans(BeanRegistryPostProcessor::class)).thenReturn(listOf(beanRegistryPostProcessor))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanConfiguration, beanRegistryPostProcessor)
		inOrder.verify(beanConfiguration).getBeanDefinitions()
		inOrder.verify(beanRegistryPostProcessor).postProcess(beanRegistry)
	}

	@Test
	fun `lookUpOptionalBean() by name and type return bean already present in the BeanRegistry`() {
		whenever(beanRegistry.lookUpOptionalBean("bean", BeanConfigurationsBeansCollectorTest::class)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpOptionalBean() by name and type processes remaining BeanDefinition to produce that bean when not present in the BeanRegistry`() {
		whenever(beanRegistry.lookUpBean("bean", BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenAnswer {
			val dependency = beanConfigurationsBeansCollector.lookUpOptionalBean("bean", BeanConfigurationsBeansCollectorTest::class)
			assertThat(dependency).isSameAs(this)
			Any()
		}
		whenever(anotherBeanDefinition.getName()).thenReturn("bean")
		whenever(anotherBeanDefinition.getType()).thenReturn(BeanConfigurationsBeansCollectorTest::class)
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(this)
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanDefinition, anotherBeanDefinition, beanRegistry)
		inOrder.verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(beanRegistry).registerBean("bean", this)
	}

	@Test
	fun `lookUpOptionalBean() by name and type returns null without such bean in the BeanRegistry nor a BeanDefinition to produce it`() {
		whenever(beanRegistry.lookUpOptionalBean("bean", BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenAnswer {
			val dependency = beanConfigurationsBeansCollector.lookUpOptionalBean("bean", BeanConfigurationsBeansCollectorTest::class)
			assertThat(dependency).isNull()
			Any()
		}
		whenever(anotherBeanDefinition.getName()).thenReturn("anotherBean")
		whenever(anotherBeanDefinition.getType()).thenReturn(Any::class)
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(Any())
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
	}

	@Test
	fun `lookUpOptionalBean() by type delegates to the BeanRegistry to return the bean`() {
		whenever(beanRegistry.lookUpOptionalBean(BeanConfigurationsBeansCollectorTest::class)).thenReturn(this)

		val bean = beanConfigurationsBeansCollector.lookUpOptionalBean(BeanConfigurationsBeansCollectorTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpOptionalBean() by type processes remaining BeanDefinition to produce that bean when not present in the BeanRegistry`() {
		whenever(beanRegistry.lookUpBean(BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenAnswer {
			val dependency = beanConfigurationsBeansCollector.lookUpOptionalBean(BeanConfigurationsBeansCollectorTest::class)
			assertThat(dependency).isSameAs(this)
			Any()
		}
		whenever(anotherBeanDefinition.getType()).thenReturn(BeanConfigurationsBeansCollectorTest::class)
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(this)
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanDefinition, anotherBeanDefinition, beanRegistry)
		inOrder.verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(beanRegistry).registerBean(this)
	}

	@Test
	fun `lookUpOptionalBean() by type returns null without such bean in the BeanRegistry nor a BeanDefinition to produce it`() {
		whenever(beanRegistry.lookUpOptionalBean(BeanConfigurationsBeansCollectorTest::class)).thenReturn(null)
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenAnswer {
			val dependency = beanConfigurationsBeansCollector.lookUpOptionalBean(BeanConfigurationsBeansCollectorTest::class)
			assertThat(dependency).isNull()
			Any()
		}
		whenever(anotherBeanDefinition.getType()).thenReturn(Any::class)
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(Any())
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
	}

	@Test
	fun `lookUpBeans() by type returns existing beans from the BeanRegistry and remaining BeanDefinitions producing such beans`() {
		val preExistingBean = BeanConfigurationsBeansCollectorTest()
		whenever(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest::class)).thenReturn(listOf(preExistingBean))
		whenever(beanRegistry.registerBean(this)).thenAnswer {
			whenever(beanRegistry.lookUpBeans(BeanConfigurationsBeansCollectorTest::class)).thenReturn(listOf(preExistingBean, this))
		}
		whenever(beanDefinition.produceBean(beanConfigurationsBeansCollector)).thenAnswer {
			val dependencies = beanConfigurationsBeansCollector.lookUpBeans(BeanConfigurationsBeansCollectorTest::class)
			assertThat(dependencies).hasSize(2)
			Unit
		}
		whenever(anotherBeanDefinition.getType()).thenReturn(BeanConfigurationsBeansCollectorTest::class)
		whenever(anotherBeanDefinition.produceBean(beanConfigurationsBeansCollector)).thenReturn(this)
		whenever(beanConfiguration.getBeanDefinitions()).thenReturn(listOf(beanDefinition, anotherBeanDefinition))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val inOrder = inOrder(beanDefinition, anotherBeanDefinition, beanRegistry)
		inOrder.verify(beanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(anotherBeanDefinition).produceBean(beanConfigurationsBeansCollector)
		inOrder.verify(beanRegistry).registerBean(this)
	}
}
