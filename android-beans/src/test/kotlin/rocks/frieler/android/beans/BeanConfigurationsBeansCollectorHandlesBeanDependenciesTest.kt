package rocks.frieler.android.beans

import assertk.Assert
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotNull
import assertk.assertions.support.expected
import java8.util.Optional
import org.junit.Test

/**
 * Tests the [BeanConfigurationsBeansCollector] in more complex situations where [BeanConfiguration]s
 * depend on each other.
 */
class BeanConfigurationsBeansCollectorHandlesBeanDependenciesTest {
	private val beanRegistry = BeanRegistry()
	private val beanConfigurationsBeansCollector = BeanConfigurationsBeansCollector(beanRegistry)

	@Test(expected = BeanInstantiationException::class)
	fun `collectBeans() fails on cyclic dependency`() {
		val beanConfiguration1 = BeanConfiguration1(SingleBeanDependency(BeanConfiguration2::class.java))
		val beanConfiguration2 = BeanConfiguration2(SingleBeanDependency(BeanConfiguration1::class.java))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration1, beanConfiguration2))
	}

	@Test
	fun `collectBeans() resolves SingleBeanDependencies between BeanConfigurations`() {
		val beanConfiguration1 = BeanConfiguration1()
		val beanConfiguration2 = BeanConfiguration2(SingleBeanDependency(BeanConfiguration1::class.java))
		val beanConfiguration3 = BeanConfiguration3(SingleBeanDependency(BeanConfiguration2::class.java))

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration2, beanConfiguration3, beanConfiguration1))

		assertThat(beanRegistry.lookUpBean(BeanConfiguration1::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration2::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration3::class.java)).isNotNull()
	}

	@Test
	fun `collectBeans() resolves transitive optional dependencies`() {
		val beanConfiguration1 = object : BeanConfiguration1(OptionalSingleBeanDependency(BeanConfiguration2::class.java)) {
			override fun defineBeans(beansCollector: BeansCollector) {
				assertThat((dependencies[0].get() as Optional<*>)).isPresent()
				super.defineBeans(beansCollector)
			}
		}
		val beanConfiguration2 = object : BeanConfiguration2(OptionalSingleBeanDependency(BeanConfiguration3::class.java)) {
			override fun defineBeans(beansCollector: BeansCollector) {
				assertThat((dependencies[0].get() as Optional<*>)).isPresent()
				super.defineBeans(beansCollector)
			}
		}
		val beanConfiguration3 = BeanConfiguration3()

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration1, beanConfiguration2, beanConfiguration3))

		assertThat(beanRegistry.lookUpBean(BeanConfiguration1::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration2::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration3::class.java)).isNotNull()
	}

	@Test
	fun `collectBeans() resolves dependencies between BeanConfigurations when one requests all beans of a type`() {
		val beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1 = BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1()
		val beanConfiguration1 = BeanConfiguration1()
		val beanConfiguration2: BeanConfiguration = BeanConfiguration2()
		val beanConfiguration3DependingOn2: BeanConfiguration = BeanConfiguration3(SingleBeanDependency(BeanConfiguration2::class.java))

		beanConfigurationsBeansCollector.collectBeans(listOf(
				beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1,
				beanConfiguration3DependingOn2,
				beanConfiguration1,
				beanConfiguration2))

		assertThat(beanRegistry.lookUpBean(BeanConfiguration1::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration2::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfiguration3::class.java)).isNotNull()
		assertThat(beanRegistry.lookUpBean(BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1::class.java)).isNotNull()
		assertThat(beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1.beanConfiguration1s).contains(beanConfiguration1)
	}

	private abstract class BeanConfigurationWithDefinedDependencies(vararg beanDependencies: BeanDependency<*>) : BeanConfiguration() {
		init {
			listOf(*beanDependencies).forEach { dependency -> addDependency(dependency) }
		}
	}

	private open class BeanConfiguration1(vararg beanDependencies: BeanDependency<*>) : BeanConfigurationWithDefinedDependencies(*beanDependencies) {
		override fun defineBeans(beansCollector: BeansCollector) {
			beansCollector.defineBean(this)
		}
	}

	private open class BeanConfiguration2(vararg beanDependencies: BeanDependency<*>) : BeanConfigurationWithDefinedDependencies(*beanDependencies) {
		override fun defineBeans(beansCollector: BeansCollector) {
			beansCollector.defineBean(this)
		}
	}

	private class BeanConfiguration3(vararg beanDependencies: BeanDependency<*>) : BeanConfigurationWithDefinedDependencies(*beanDependencies) {
		override fun defineBeans(beansCollector: BeansCollector) {
			beansCollector.defineBean(this)
		}
	}

	private class BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1 : BeanConfiguration() {
		private val beanConfiguration1sDependency: BeanDependency<List<BeanConfiguration1>> = requireBeans(BeanConfiguration1::class.java)

		lateinit var beanConfiguration1s: List<BeanConfiguration1>

		override fun defineBeans(beansCollector: BeansCollector) {
			beanConfiguration1s = beanConfiguration1sDependency.get()!!
			beansCollector.defineBean(this)
		}
	}
}

fun Assert<Optional<*>>.isPresent() = transform { actual ->
	if (!actual.isPresent) {
		expected("to be present")
	}
}
