package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test

class BeansTest {
    private val beansProvider: BeansProvider = mock()

    @Before
    fun configureMockBeansProvider() {
        Beans.setBeans(beansProvider)
    }

    @Test
    fun `lookUpBean() by name and type delegates to the BeansProvider`() {
        val name = "bean"
        val bean = Any()
        whenever(beansProvider.lookUpBean(name, Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpBean(name, Any::class.java)

        assertThat(retrievedBean).isSameAs(bean)
    }

    @Test
    fun `lookUpBean() by name and type returns null without such bean`() {
        val name = "bean"
        whenever(beansProvider.lookUpBean(name, Any::class)).thenReturn(null)

        val retrievedBean = Beans.lookUpBean(name, Any::class.java)

        assertThat(retrievedBean).isNull()
    }

    @Test
    fun `lookUpBean() by type delegates to the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBean(Any::class)).thenReturn(bean)

        val retrievedBean = Beans.lookUpBean(Any::class.java)

        assertThat(retrievedBean).isSameAs(bean)
    }

    @Test
    fun `lookUpBean() by type returns null without such bean`() {
        whenever(beansProvider.lookUpBean(Any::class)).thenReturn(null)

        val retrievedBean = Beans.lookUpBean(Any::class.java)

        assertThat(retrievedBean).isNull()
    }

    @Test
    fun `lookUpBeans() by type delegates to the BeansProvider`() {
        val bean = Any()
        whenever(beansProvider.lookUpBeans(Any::class)).thenReturn(listOf(bean))

        val retrievedBeans = Beans.lookUpBeans(Any::class.java)

        assertThat(retrievedBeans).containsExactly(bean)
    }
}
