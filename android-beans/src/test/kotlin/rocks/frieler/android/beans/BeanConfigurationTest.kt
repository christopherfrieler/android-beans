package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.contains
import com.nhaarman.mockitokotlin2.mock
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
