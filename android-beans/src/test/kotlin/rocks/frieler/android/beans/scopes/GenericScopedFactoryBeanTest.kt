package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java8.util.function.Supplier
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class GenericScopedFactoryBeanTest {
    private val scope = "aScope"
    private val producer: Supplier<GenericScopedFactoryBeanTest> = mock();
    private val factoryBean = GenericScopedFactoryBean(scope, GenericScopedFactoryBeanTest::class.java, producer)

    @Test
    fun `getScope() returns configured scope`() {
        assertThat(factoryBean.scope).isEqualTo(scope)
    }

    @Test
    fun `getType() returns configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(GenericScopedFactoryBeanTest::class.java)
    }

    @Test
    fun `produceBean() calls configured producer`() {
        whenever(producer.get()).thenReturn(this)

        assertThat(factoryBean.produceBean()).isEqualTo(this)
    }
}
