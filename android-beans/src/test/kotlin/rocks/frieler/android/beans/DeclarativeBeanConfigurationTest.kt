package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Test

class DeclarativeBeanConfigurationTest {
	private val dependencyProvider: BeansProvider = mock()

	@Test
	fun `DeclarativeBeanConfiguration can define a bean directly through a BeanDefinition`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			val beanDefinition = mock<BeanDefinition<Any>>()

			override fun beans() {
				bean(
						beanDefinition
				)
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).contains(aBeanConfiguration.beanDefinition)
	}

	@Test
	fun `DeclarativeBeanConfiguration can define beans with and without name`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean {
					this@DeclarativeBeanConfigurationTest
				}

				bean("named_bean") {
					this@DeclarativeBeanConfigurationTest
				}
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(2)
		assertThat(beanDefinitions[0].getName()).isNull()
		assertThat(beanDefinitions[0].produceBean(dependencyProvider)).isSameAs(this)
		assertThat(beanDefinitions[1].getName()).isEqualTo("named_bean")
		assertThat(beanDefinitions[1].produceBean(dependencyProvider)).isSameAs(this)
	}

	@Test
	fun `DeclarativeBeanConfiguration allows the bean-definition to use the dependency-provider`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean {
					assertThat(this).isInstanceOf(BeansProvider::class)
					this@DeclarativeBeanConfigurationTest
				}
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(1)
		assertThat(beanDefinitions[0].produceBean(dependencyProvider)).isSameAs(this)
	}

	@Test
	fun `DeclarativeBeanConfiguration can define a bean with explicit Java type for Java interoperability`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				val beanDefinition = { this@DeclarativeBeanConfigurationTest }
				bean("bean", Any::class.java, beanDefinition)
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(1)
		assertThat(beanDefinitions[0].getName()).isEqualTo("bean")
		assertThat(beanDefinitions[0].getType()).isEqualTo(Any::class)
		assertThat(beanDefinitions[0].produceBean(dependencyProvider)).isSameAs(this)
	}

	@Test
	fun `DeclarativeBeanConfiguration can define a bean with explicit Java type using the dependency-provider for Java interoperability`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				val beanDefinition: BeansProvider.() -> Any = {
					assertThat(this).isInstanceOf(BeansProvider::class)
					this@DeclarativeBeanConfigurationTest
				}
				bean("bean", Any::class.java, beanDefinition)
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(1)
		assertThat(beanDefinitions[0].getName()).isEqualTo("bean")
		assertThat(beanDefinitions[0].getType()).isEqualTo(Any::class)
		assertThat(beanDefinitions[0].produceBean(dependencyProvider)).isSameAs(this)
	}
}
