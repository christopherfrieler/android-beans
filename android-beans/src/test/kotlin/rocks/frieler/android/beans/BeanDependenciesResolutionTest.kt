package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import assertk.assertions.isNotNull
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import rocks.frieler.android.beans.scopes.singleton.lazyInstantiatedBean

/**
 * Tests more complex situations where [BeanConfiguration]s depend on each other.
 */
class BeanDependenciesResolutionTest {
	private val beanRegistry = BeanRegistry()
	private val beanConfigurationsBeansCollector = BeanConfigurationsBeansCollector(beanRegistry)

	@Test
	fun `resolves dependency to a bean from another BeanConfiguration`() {
		val beanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean { BeanWithDependency(lookUpBean(Bean::class)) }
			}
		}

		val anotherBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean { Bean() }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration))

		val bean = beanRegistry.lookUpBean(Bean::class)
		assertThat(bean).isNotNull()
		val beanWithDependency = beanRegistry.lookUpBean(BeanWithDependency::class)
		assertThat(beanWithDependency).isNotNull()
		assertThat(beanWithDependency.dependency).isSameAs(bean)
	}

	@Test
	fun `resolves chained dependencies between BeanConfigurations`() {
		val beanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean") { Bean() }
			}
		}

		val anotherBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("anotherBean") {
					BeanWithDependency(lookUpBean("bean", Bean::class))
				}
			}
		}

		val yetAnotherBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("yetAnotherBean") {
					BeanWithDependency(lookUpBean("anotherBean", BeanWithDependency::class))
				}
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(anotherBeanConfiguration, yetAnotherBeanConfiguration, beanConfiguration))

		val bean = beanRegistry.lookUpBean("bean", Bean::class)
		assertThat(bean).isNotNull()
		val anotherBean = beanRegistry.lookUpBean("anotherBean", BeanWithDependency::class)
		assertThat(anotherBean).isNotNull()
		assertThat(anotherBean.dependency).isSameAs(bean)
		val yetAnotherBean = beanRegistry.lookUpBean("yetAnotherBean", BeanWithDependency::class)
		assertThat(yetAnotherBean).isNotNull()
		assertThat(yetAnotherBean.dependency).isSameAs(anotherBean)
	}

	@Test
	fun `resolves optional dependencies if available`() {
		val beanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("presentBean") { Bean() }

				bean {
					val beans = mutableListOf<Bean>()
					lookUpOptionalBean("presentBean", Bean::class)?.apply { beans.add(this) }
					lookUpOptionalBean("missingBean", Bean::class)?.apply { beans.add(this) }
					BeanWithDependencies(beans)
				}
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val presentBean = beanRegistry.lookUpBean("presentBean", Bean::class)
		assertThat(presentBean).isNotNull()

		val beanWithDependencies = beanRegistry.lookUpBean(BeanWithDependencies::class)
		assertThat(beanWithDependencies).isNotNull()
		assertThat(beanWithDependencies.dependencies).containsExactly(presentBean)
	}

	@Test
	fun `resolves dependencies to all beans of a type from the same and other BeanConfigurations`() {
		val beanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean") { Bean() }

				bean {
					BeanWithDependencies(lookUpBeans(Bean::class))
				}
			}
		}

		val anotherBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("anotherBean") {
					Bean()
				}
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration, anotherBeanConfiguration))

		val bean = beanRegistry.lookUpBean("bean", Bean::class)
		assertThat(bean).isNotNull()

		val anotherBean = beanRegistry.lookUpBean("anotherBean", Bean::class)
		assertThat(anotherBean).isNotNull()

		val beans = beanRegistry.lookUpBeans(Bean::class)
		assertThat(beans).containsOnly(bean, anotherBean)

		val beanWithDependencies = beanRegistry.lookUpBean(BeanWithDependencies::class)
		assertThat(beanWithDependencies).isNotNull()
		assertThat(beanWithDependencies.dependencies).containsAll(bean, anotherBean)
	}

	@Test
	//@Ignore("known limitation")
	fun `resolves dependency to a scoped bean defined later`() {
		val beanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean {
					BeanWithDependency(lookUpBean(Bean::class))
				}

				lazyInstantiatedBean { Bean() }
			}
		}

		beanConfigurationsBeansCollector.collectBeans(listOf(beanConfiguration))

		val bean = beanRegistry.lookUpBean(Bean::class)
		assertThat(bean).isNotNull()

		val beanWithDependency = beanRegistry.lookUpBean(BeanWithDependency::class)
		assertThat(beanWithDependency).isNotNull()
		assertThat(beanWithDependency.dependency).isSameAs(bean)
	}
}

private open class Bean

private class BeanWithDependency(val dependency : Any)

private class BeanWithDependencies(val dependencies : List<*>)
