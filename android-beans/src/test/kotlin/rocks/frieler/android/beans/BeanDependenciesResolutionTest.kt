package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotNull
import assertk.assertions.isSameAs
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
		val beanConfiguration1 = object : SingleBeanConfiguration() {
			override fun beans() {
				bean("bean1") { theBean }
			}
		}

		val beanConfiguration2 = object : SingleBeanConfiguration() {
			init {
				requireBean("bean1", Any::class.java)
			}

			override fun beans() {
				bean("bean2") { theBean }
			}
		}

		val beanConfiguration3 = object : SingleBeanConfiguration() {
			init {
				requireBean("bean2", Any::class.java)
			}

			override fun beans() {
				bean("bean3") { theBean }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration2, beanConfiguration3, beanConfiguration1))

		assertThat(beanRegistry.lookUpBean("bean1", Any::class)).isSameAs(beanConfiguration1.theBean)
		assertThat(beanRegistry.lookUpBean("bean2", Any::class)).isSameAs(beanConfiguration2.theBean)
		assertThat(beanRegistry.lookUpBean("bean3", Any::class)).isSameAs(beanConfiguration3.theBean)
	}

	@Test
	fun `resolves transitive optional dependencies`() {
		val beanConfiguration1 = object : SingleBeanConfiguration() {
			val dependencyToBean2 = requireOptionalBean("bean2", Any::class)
			override fun beans() {
				bean("bean1") {
					assertThat(lookUpBean("bean2", Any::class)).isNotNull()
					theBean
				}
			}
		}

		val beanConfiguration2 = object : SingleBeanConfiguration() {
			val dependencyToBean3 = requireOptionalBean("bean3", Any::class)
			override fun beans() {
				bean("bean2") {
					assertThat(lookUpBean("bean3", Any::class)).isNotNull()
					theBean
				}
			}
		}

		val beanConfiguration3 = object : SingleBeanConfiguration() {
			override fun beans() {
				bean("bean3") { theBean }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration1, beanConfiguration2, beanConfiguration3))

		assertThat(beanRegistry.lookUpBean("bean1", Any::class)).isSameAs(beanConfiguration1.theBean)
		assertThat(beanRegistry.lookUpBean("bean2", Any::class)).isSameAs(beanConfiguration2.theBean)
		assertThat(beanRegistry.lookUpBean("bean3", Any::class)).isSameAs(beanConfiguration3.theBean)
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
				bean {
					beanDependenciesResolutionTestBeans = lookUpBeans(BeanDependenciesResolutionTest::class)
					Unit
				}
			}
		}

		val aBeanConfiguration = object : SingleBeanConfiguration() {
			override fun beans() {
				bean("a_bean") { theBean }
			}
		}

		val anotherBeanConfiguration = object : SingleBeanConfiguration() {
			init {
				requireBean("a_bean", Any::class)
			}

			override fun beans() {
				bean("another_bean") { theBean }
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
		assertThat(beanRegistry.lookUpBean("a_bean", Any::class)).isSameAs(aBeanConfiguration.theBean)
		assertThat(beanRegistry.lookUpBean("another_bean", Any::class)).isSameAs(anotherBeanConfiguration.theBean)
	}
}

abstract class SingleBeanConfiguration : DeclarativeBeanConfiguration() {
	val theBean = Any()
}
