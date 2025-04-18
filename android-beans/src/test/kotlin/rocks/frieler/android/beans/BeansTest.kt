package rocks.frieler.android.beans

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BeansTest {
    private val beansProvider: BeansProvider = mock()

    @BeforeEach
    fun configureMockBeansProvider() {
        Beans.setBeans(beansProvider)
    }

    @Test
    fun `lookUpBean() by given type fetches bean from the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBean(Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpBean(type = Any::class)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpBean() by given type rethrows NoSuchBeanException without such bean`() {
        whenever(beansProvider.lookUpBean(Any::class)).thenThrow(NoSuchBeanException(Any::class))

        assertFailure {
            Beans.lookUpBean(type = Any::class)
        }.hasClass(NoSuchBeanException::class)
    }

    @Test
    fun `lookUpBean() by inferred type fetches bean from the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBean(Any::class)).thenReturn(bean)

        val retrievedBean: Any? = Beans.lookUpBean()

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpBean() by given name and type fetches bean from the BeansProvider`() {
        val name = "bean"
        val bean = Any()
        whenever(beansProvider.lookUpBean(name, Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpBean(name, Any::class)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpBean() by given name and type rethrows NoSuchBeanException without such bean`() {
        val name = "bean"
        whenever(beansProvider.lookUpBean(name, Any::class)).thenThrow(NoSuchBeanException(name, Any::class))

        assertFailure {
            Beans.lookUpBean(name, Any::class)
        }.hasClass(NoSuchBeanException::class)
    }

    @Test
    fun `lookUpBean() by given name and  inferred type fetches bean from the BeansProvider`() {
        val name = "bean"
        val bean = Any()
        whenever(beansProvider.lookUpBean(name, Any::class)).thenReturn(bean)

        val retrievedBean: Any? = Beans.lookUpBean(name)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpOptionalBean() by given type fetches bean from the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpOptionalBean(Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpOptionalBean(type = Any::class)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpOptionalBean() by given type returns null without such bean`() {
        whenever(beansProvider.lookUpOptionalBean(Any::class)).thenReturn(null)

        val retrievedBean = Beans.lookUpOptionalBean(type = Any::class)

        assertThat(retrievedBean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by inferred type fetches bean from the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpOptionalBean(Any::class)).thenReturn(bean)

        val retrievedBean: Any? = Beans.lookUpOptionalBean()

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpOptionalBean() by given name and type fetches bean from the BeansProvider`() {
        val name = "bean"
        val bean = Any()
        whenever(beansProvider.lookUpOptionalBean(name, Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpOptionalBean(name, Any::class)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpOptionalBean() by given name and type returns null without such bean`() {
        val name = "bean"
        whenever(beansProvider.lookUpOptionalBean(name, Any::class)).thenReturn(null)

        val retrievedBean = Beans.lookUpOptionalBean(name, Any::class)

        assertThat(retrievedBean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by given name and  inferred type fetches bean from the BeansProvider`() {
        val name = "bean"
        val bean = Any()
        whenever(beansProvider.lookUpOptionalBean(name, Any::class)).thenReturn(bean)

        val retrievedBean: Any? = Beans.lookUpOptionalBean(name)

        assertThat(retrievedBean).isSameInstanceAs(bean)
    }

    @Test
    fun `lookUpBeans() by given type delegates to the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBeans(Any::class)).thenReturn(listOf(bean))

        val retrievedBeans = Beans.lookUpBeans(Any::class)

        assertThat(retrievedBeans).containsExactly(bean)
    }

    @Test
    fun `lookUpBeans() by inferred type delegates to the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBeans(Any::class)).thenReturn(listOf(bean))

        val retrievedBeans: List<Any> = Beans.lookUpBeans()

        assertThat(retrievedBeans).containsExactly(bean)
    }
}
