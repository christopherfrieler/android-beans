package rocks.frieler.android.beans.scopes.singleton

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
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.Companion.lazy

@RunWith(MockitoJUnitRunner::class)
class SingletonScopedFactoryBeanTest {
    private val producer: Supplier<SingletonScopedFactoryBeanTest> = mock()
    private val factoryBean = lazy(SingletonScopedFactoryBeanTest::class.java, producer)

    @Test
    fun `scope() is singleton scope`() {
        assertThat(factoryBean.scope).isEqualTo(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE)
    }

    @Test
    fun `beanType is the configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(SingletonScopedFactoryBeanTest::class.java)
    }

    @Test
    fun `produceBean() calls the configured producer`() {
        whenever(producer.get()).thenReturn(this)

        val producedBean = factoryBean.produceBean()

        verify(producer).get()
        assertThat(producedBean).isSameAs(this)
    }
}
