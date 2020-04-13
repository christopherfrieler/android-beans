package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import org.junit.Test

class DeclarativeBeanConfigurationTest {
	@Test
	fun `BeanConfiguration can define beans with and without name`() {
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
		assertThat(beanDefinitions[0].produceBean()).isSameAs(this)
		assertThat(beanDefinitions[1].getName()).isEqualTo("named_bean")
		assertThat(beanDefinitions[1].produceBean()).isSameAs(this)
	}

	@Test
	fun `BeanConfiguration can define a bean with explicit Java type for Java interoperability`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean", Any::class.java) {
					this@DeclarativeBeanConfigurationTest
				}
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(1)
		assertThat(beanDefinitions[0].getName()).isEqualTo("bean")
		assertThat(beanDefinitions[0].getType()).isEqualTo(Any::class)
		assertThat(beanDefinitions[0].produceBean()).isSameAs(this)
	}

	@Test
	fun `BeanConfiguration can define a bean from a Pair of Java type and definition for Java interoperability`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean", Pair(Any::class.java, { this@DeclarativeBeanConfigurationTest }))
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(1)
		assertThat(beanDefinitions[0].getName()).isEqualTo("bean")
		assertThat(beanDefinitions[0].getType()).isEqualTo(Any::class)
		assertThat(beanDefinitions[0].produceBean()).isSameAs(this)
	}

	@Test
	fun `Defining a bean returns a BeanReference to use the bean as a dependency for further beans`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				val beanRef = bean("bean") {
					this@DeclarativeBeanConfigurationTest
				}

				bean("anotherBean") {
					beanRef.use()
				}
			}
		}

		val beanDefinitions = aBeanConfiguration.getBeanDefinitions()

		assertThat(beanDefinitions).hasSize(2)
		assertThat(beanDefinitions[0].getName()).isEqualTo("bean")
		assertThat(beanDefinitions[0].produceBean()).isSameAs(this)
		assertThat(beanDefinitions[1].getName()).isEqualTo("anotherBean")
		assertThat(beanDefinitions[1].produceBean()).isSameAs(this)
	}
}
