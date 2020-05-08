package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeansProvider


@RunWith(MockitoJUnitRunner::class)
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
        assertThat(producedBean).isSameAs(this)
    }
}
