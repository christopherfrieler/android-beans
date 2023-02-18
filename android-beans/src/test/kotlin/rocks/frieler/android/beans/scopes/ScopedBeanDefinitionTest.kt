package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.mockito.kotlin.mock
import org.junit.jupiter.api.Test

class ScopedBeanDefinitionTest {
	private val scopedBeanDefinition = ScopedBeanDefinition(factoryBeanType = ScopedFactoryBean::class, targetType = ScopedBeanDefinitionTest::class) { mock() }

	@Test
	fun `ScopedBeanDefinition can produce a ScopedFactoryBean`() {
		assertThat(scopedBeanDefinition.canProduce(ScopedFactoryBean::class)).isTrue()
	}

	@Test
	fun `ScopedBeanDefinition can produce a bean of the target-type`() {
		assertThat(scopedBeanDefinition.canProduce(ScopedBeanDefinitionTest::class)).isTrue()
	}

	@Test
	fun `ScopedBeanDefinition cannot produce a bean of some random type`() {
		assertThat(scopedBeanDefinition.canProduce(Unit::class)).isFalse()
	}
}
