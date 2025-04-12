package rocks.frieler.android.beans

import assertk.Assert
import assertk.all
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import org.mockito.kotlin.whenever
import kotlin.reflect.KClass

class BeansProviderTest {
	private val beansProvider = spy(object : BeansProvider {
		override fun <T : Any> lookUpOptionalBean(name: String, type: KClass<T>): T? = null
		override fun <T : Any> lookUpOptionalBean(type: KClass<T>): T? = null
		override fun <T : Any> lookUpBeans(type: KClass<T>): List<T> = emptyList()
	})

	@Test
	fun `lookUpBean() by name and type returns optionally present bean`() {
		whenever(beansProvider.lookUpOptionalBean("name", BeansProviderTest::class)).thenReturn(this)

		val bean = beansProvider.lookUpBean("name", BeansProviderTest::class)

		assertThat(bean).isSameInstanceAs(this)
	}

	@Test
	fun `lookUpBean() by name and type throws NoSuchBeanException when optionally present bean is not present`() {
		whenever(beansProvider.lookUpOptionalBean("name", BeansProviderTest::class)).thenReturn(null)

		val exception = assertFailure {
			beansProvider.lookUpBean("name", BeansProviderTest::class)
		}

		val noSuchBeanExceptionAssert = exception.let {
			it.hasClass(NoSuchBeanException::class)
			@Suppress("UNCHECKED_CAST")
			it as Assert<NoSuchBeanException>
		}

		noSuchBeanExceptionAssert.all {
			prop(NoSuchBeanException::name).isEqualTo("name")
			prop(NoSuchBeanException::type).isEqualTo(BeansProviderTest::class)
		}
	}

	@Test
	fun `lookUpBean() by type returns optionally present bean`() {
		whenever(beansProvider.lookUpOptionalBean(BeansProviderTest::class)).thenReturn(this)

		val bean = beansProvider.lookUpBean(BeansProviderTest::class)

		assertThat(bean).isSameInstanceAs(this)
	}

	@Test
	fun `lookUpBean() by type throws NoSuchBeanException when optionally present bean is not present`() {
		whenever(beansProvider.lookUpOptionalBean(BeansProviderTest::class)).thenReturn(null)

		val exception = assertFailure {
			beansProvider.lookUpBean(BeansProviderTest::class)
		}

		exception.all {
			hasClass(NoSuchBeanException::class)
			prop("name") { (it as NoSuchBeanException).name }.isNull()
			prop("type") { (it as NoSuchBeanException).type }.isEqualTo(BeansProviderTest::class)
		}
	}
}
