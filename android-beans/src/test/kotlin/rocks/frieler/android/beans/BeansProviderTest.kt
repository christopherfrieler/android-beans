package rocks.frieler.android.beans

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import assertk.assertions.prop
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
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

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by name and type throws NoSuchBeanException when optionally present bean is not present`() {
		whenever(beansProvider.lookUpOptionalBean("name", BeansProviderTest::class)).thenReturn(null)

		val exception = assertThat {
			beansProvider.lookUpBean("name", BeansProviderTest::class)
		}.isFailure()

		exception.all {
			hasClass(NoSuchBeanException::class)
			prop(NoSuchBeanException::name).isEqualTo("name")
			prop(NoSuchBeanException::type).isEqualTo(BeansProviderTest::class)
		}
	}

	@Test
	fun `lookUpBean() by type returns optionally present bean`() {
		whenever(beansProvider.lookUpOptionalBean(BeansProviderTest::class)).thenReturn(this)

		val bean = beansProvider.lookUpBean(BeansProviderTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpBean() by type throws NoSuchBeanException when optionally present bean is not present`() {
		whenever(beansProvider.lookUpOptionalBean(BeansProviderTest::class)).thenReturn(null)

		val exception = assertThat {
			beansProvider.lookUpBean(BeansProviderTest::class)
		}.isFailure()

		exception.all {
			hasClass(NoSuchBeanException::class)
			prop(NoSuchBeanException::name).isNull()
			prop(NoSuchBeanException::type).isEqualTo(BeansProviderTest::class)
		}
	}
}
