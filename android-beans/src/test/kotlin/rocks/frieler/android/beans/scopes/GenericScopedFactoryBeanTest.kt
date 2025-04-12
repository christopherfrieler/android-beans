package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeansProvider


class GenericScopedFactoryBeanTest {
    private val scope = "aScope"
    private val producer: BeansProvider.() -> GenericScopedFactoryBeanTest = mock()
    private val factoryBean = GenericScopedFactoryBean(scope, GenericScopedFactoryBeanTest::class, producer)

    @Test
    fun `getScope() returns configured scope`() {
        assertThat(factoryBean.scope).isEqualTo(scope)
    }

    @Test
    fun `getType() returns configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(GenericScopedFactoryBeanTest::class)
    }

    @Test
    fun `produceBean() calls the configured producer`() {
        val beansProvider: BeansProvider = mock()
        whenever(producer(beansProvider)).thenReturn(this)

        val producedBean = factoryBean.produceBean(beansProvider)

        verify(producer).invoke(beansProvider)
        assertThat(producedBean).isSameInstanceAs(this)
    }
}
