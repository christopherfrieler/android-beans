package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class BeanConfigurationTest {
	private val beanConfiguration = object : BeanConfiguration() {}

	@Test
	fun `addBeanDefinition() creates, adds and returns a BeanDefinition`() {
		val beanDefinition = beanConfiguration.addBeanDefinition("name", BeanConfigurationTest::class) { this }

		assertThat(beanConfiguration.getBeanDefinitions()).containsExactly(beanDefinition)
		with(beanDefinition) {
			assertThat(getName()).isEqualTo("name")
			assertThat(getType()).isEqualTo(BeanConfigurationTest::class)
			assertThat(produceBean(mock())).isSameAs(this@BeanConfigurationTest)
		}
	}
}
