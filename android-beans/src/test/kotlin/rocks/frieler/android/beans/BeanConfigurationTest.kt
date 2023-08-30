package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import org.mockito.kotlin.mock
import org.junit.jupiter.api.Test

class BeanConfigurationTest {
	private val beanConfiguration = object : BeanConfiguration() {}

	@Test
	fun `addBeanDefinition() adds a BeanDefinition`() {
		val beanDefinition = mock<BeanDefinition<BeanConfigurationTest>>()

		beanConfiguration.addBeanDefinition(beanDefinition)

		assertThat(beanConfiguration.getBeanDefinitions()).contains(beanDefinition)
	}
}
