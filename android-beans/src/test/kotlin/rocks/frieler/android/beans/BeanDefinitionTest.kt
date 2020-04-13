package rocks.frieler.android.beans

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class BeanDefinitionTest {
	private val beanName = "name"
	private val beanType = BeanDefinitionTest::class
	private val beanCreator: () -> BeanDefinitionTest = mock()

	private val beanDefinition = BeanDefinition(beanName, beanType, beanCreator)

	@Test
	fun `getName() returns the bean-name`() {
		assertThat(beanDefinition.getName()).isEqualTo(beanName)
	}

	@Test
	fun `getType() returns the bean-type`() {
		assertThat(beanDefinition.getType()).isEqualTo(beanType)
	}

	@Test
	fun `produceBean() invokes the creator and returns the produced bean`() {
		whenever(beanCreator.invoke()).thenReturn(this)

		val bean = beanDefinition.produceBean()

		verify(beanCreator).invoke()
		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `produceBean() cannot be invoked twice`() {
		whenever(beanCreator.invoke()).thenReturn(this)

		beanDefinition.produceBean()
		assertThat {
			beanDefinition.produceBean()
		}.isFailure().all {
			hasClass(IllegalStateException::class)
			hasMessage("the bean was already produced.")
		}
	}

	@Test
	fun `use() cannot be invoked before the bean was produced`() {
		assertThat {
			beanDefinition.use()
		}.isFailure().all {
			hasClass(IllegalStateException::class)
			hasMessage("the bean was not produced yet.")
		}
	}

	@Test
	fun `use() returns the produced bean`() {
		whenever(beanCreator.invoke()).thenReturn(this)

		val producedBean = beanDefinition.produceBean()
		val bean = beanDefinition.use()

		assertThat(bean).isSameAs(producedBean)
	}
}