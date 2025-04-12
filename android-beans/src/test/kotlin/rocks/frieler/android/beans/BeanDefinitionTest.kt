package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BeanDefinitionTest {
	private val beanName = "name"
	private val beanType = BeanDefinitionTest::class
	private val beanCreator: BeansProvider.() -> BeanDefinitionTest = mock()

	private val beanDefinition = BeanDefinition(beanName, beanType, beanCreator)

	private val dependencyProvider: BeansProvider = mock()

	@Test
	fun `getName() returns the bean-name`() {
		assertThat(beanDefinition.getName()).isEqualTo(beanName)
	}

	@Test
	fun `getType() returns the bean-type`() {
		assertThat(beanDefinition.getType()).isEqualTo(beanType)
	}

	@Test
	fun `canProduce() a bean of the beanType`() {
		assertThat(beanDefinition.canProduce(beanType)).isTrue()
	}

	@Test
	fun `canProduce() a bean of a super-type of the beanType`() {
		assertThat(beanDefinition.canProduce(Any::class)).isTrue()
	}

	@Test
	fun `canProduce() declines to be able to produce a bean of any other type`() {
		assertThat(beanDefinition.canProduce(Unit::class)).isFalse()
	}

	@Test
	fun `produceBean() invokes the creator and returns the produced bean`() {
		whenever(beanCreator.invoke(dependencyProvider)).thenReturn(this)

		val bean = beanDefinition.produceBean(dependencyProvider)

		verify(beanCreator).invoke(dependencyProvider)
		assertThat(bean).isSameInstanceAs(this)
	}
}
