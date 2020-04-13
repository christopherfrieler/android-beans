package rocks.frieler.android.beans

import assertk.Assert
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isSameAs
import assertk.assertions.support.expected
import java8.util.Optional
import org.junit.Test

/**
 * Tests more complex situations where [BeanConfiguration]s depend on each other.
 */
class BeanDependenciesResolutionTest {
	private val beanRegistry = BeanRegistry()
	private val beanConfigurationsBeansCollector = BeanConfigurationsBeansCollector(beanRegistry)

	@Test(expected = BeanInstantiationException::class)
	fun `fails on cyclic dependency`() {
		val beanConfiguration1 = object : DeclarativeBeanConfiguration() {
			init {
				requireBean("bean2", Any::class.java)
			}

			override fun beans() {
				bean("bean1") { this }
			}
		}

		val beanConfiguration2 = object : DeclarativeBeanConfiguration() {
			init {
				requireBean("bean1", Any::class.java)
			}

			override fun beans() {
				bean("bean2") { this }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration1, beanConfiguration2))
	}

	@Test
	fun `resolves SingleBeanDependencies between BeanConfigurations`() {
		val beanConfiguration1 = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean1") { this }
			}
		}

		val beanConfiguration2 = object : DeclarativeBeanConfiguration() {
			init {
				requireBean("bean1", Any::class.java)
			}

			override fun beans() {
				bean("bean2") { this }
			}
		}

		val beanConfiguration3 = object : DeclarativeBeanConfiguration() {
			init {
				requireBean("bean2", Any::class.java)
			}

			override fun beans() {
				bean("bean3") { this }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration2, beanConfiguration3, beanConfiguration1))

		assertThat(beanRegistry.lookUpBean("bean1", Any::class)).isSameAs(beanConfiguration1)
		assertThat(beanRegistry.lookUpBean("bean2", Any::class)).isSameAs(beanConfiguration2)
		assertThat(beanRegistry.lookUpBean("bean3", Any::class)).isSameAs(beanConfiguration3)
	}

	@Test
	fun `resolves transitive optional dependencies`() {
		val beanConfiguration1 = object : DeclarativeBeanConfiguration() {
			val dependencyToBean2 = requireOptionalBean("bean2", Any::class)
			override fun beans() {
				assertThat((dependencyToBean2.get() as Optional<*>)).isPresent()
				bean("bean1") { this }
			}
		}

		val beanConfiguration2 = object : DeclarativeBeanConfiguration() {
			val dependencyToBean3 = requireOptionalBean("bean3", Any::class)
			override fun beans() {
				assertThat((dependencyToBean3.get() as Optional<*>)).isPresent()
				bean("bean2") { this }
			}
		}

		val beanConfiguration3 = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean3") { this }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration1, beanConfiguration2, beanConfiguration3))

		assertThat(beanRegistry.lookUpBean("bean1", Any::class)).isSameAs(beanConfiguration1)
		assertThat(beanRegistry.lookUpBean("bean2", Any::class)).isSameAs(beanConfiguration2)
		assertThat(beanRegistry.lookUpBean("bean3", Any::class)).isSameAs(beanConfiguration3)
	}

	@Test
	fun `resolves dependencies between BeanConfigurations when one requests all beans of a type`() {
		val beanConfigurationDefiningThisTestAsBean = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean { this@BeanDependenciesResolutionTest }
			}
		}

		val beanConfigurationNeedingAllBeanDependenciesResolutionTestBeans = object : DeclarativeBeanConfiguration() {
			val dependency = requireBeans(BeanDependenciesResolutionTest::class)
			lateinit var beanDependenciesResolutionTestBeans: List<BeanDependenciesResolutionTest>
			override fun beans() {
				beanDependenciesResolutionTestBeans = dependency.get()!!
			}
		}

		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("a_bean") { this }
			}
		}

		val anotherBeanConfiguration = object : DeclarativeBeanConfiguration() {
			init {
				requireBean("a_bean", Any::class.java)
			}

			override fun beans() {
				bean("another_bean") { this }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(
				beanConfigurationDefiningThisTestAsBean,
				beanConfigurationNeedingAllBeanDependenciesResolutionTestBeans,
				aBeanConfiguration,
				anotherBeanConfiguration))

		assertThat(beanRegistry.lookUpBean(BeanDependenciesResolutionTest::class)).isSameAs(this)
		assertThat(beanConfigurationNeedingAllBeanDependenciesResolutionTestBeans.beanDependenciesResolutionTestBeans)
				.contains(this)
		assertThat(beanRegistry.lookUpBean("a_bean", Any::class)).isSameAs(aBeanConfiguration)
		assertThat(beanRegistry.lookUpBean("another_bean", Any::class)).isSameAs(anotherBeanConfiguration)
	}
}

fun Assert<Optional<*>>.isPresent() = transform { actual ->
	if (!actual.isPresent) {
		expected("to be present")
	}
}
