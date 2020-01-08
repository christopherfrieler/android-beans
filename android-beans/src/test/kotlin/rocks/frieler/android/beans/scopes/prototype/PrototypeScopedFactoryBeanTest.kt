package rocks.frieler.android.beans.scopes.prototype

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java8.util.function.Supplier
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.Companion.prototype

@RunWith(MockitoJUnitRunner::class)
class PrototypeScopedFactoryBeanTest {
    private val producer: Supplier<PrototypeScopedFactoryBeanTest> = mock()
    private val factoryBean = prototype(PrototypeScopedFactoryBeanTest::class.java, producer)

    @Test
    fun `scope() is prototype scope`() {
        assertThat(factoryBean.scope).isEqualTo(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE)
    }

    @Test
    fun `beanType is the configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class.java)
    }

    @Test
    fun `produceBean() calls the configured producer`() {
        whenever(producer.get()).thenReturn(this)

        val producedBean = factoryBean.produceBean()

        verify(producer).get()
        assertThat(producedBean).isSameAs(this)
    }
}
