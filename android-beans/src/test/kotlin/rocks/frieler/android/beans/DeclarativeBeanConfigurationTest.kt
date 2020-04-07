package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isNotNull
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import rocks.frieler.android.beans.scopes.singleton.lazyInstantiatedBean

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

		val beansCollector : BeansCollector = mock()
		aBeanConfiguration.defineBeans(beansCollector)

		verify(beansCollector, times(1)).defineBean(this)
		verify(beansCollector, times(1)).defineBean("named_bean", this)
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

		val beansCollector : BeansCollector = mock()
		aBeanConfiguration.defineBeans(beansCollector)

		verify(beansCollector, times(1)).defineBean("bean", this)
	}

	@Test
	fun `BeanConfiguration can define a bean from a Pair of Java type and definition for Java interoperability`() {
		val aBeanConfiguration = object : DeclarativeBeanConfiguration() {
			override fun beans() {
				bean("bean", Pair(Any::class.java, { this@DeclarativeBeanConfigurationTest }))
			}
		}

		val beansCollector : BeansCollector = mock()
		aBeanConfiguration.defineBeans(beansCollector)

		verify(beansCollector, times(1)).defineBean("bean", this)
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

		val beansCollector : BeansCollector = mock()
		aBeanConfiguration.defineBeans(beansCollector)

		verify(beansCollector).defineBean("anotherBean", this)
	}
}
