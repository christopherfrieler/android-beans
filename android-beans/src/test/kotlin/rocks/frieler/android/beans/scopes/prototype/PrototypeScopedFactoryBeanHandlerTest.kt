package rocks.frieler.android.beans.scopes.prototype

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PrototypeScopedFactoryBeanHandlerTest {

    private val prototypeScopedFactoryBeanHandler = PrototypeScopedFactoryBeanHandler()

    private val scopedFactoryBean: PrototypeScopedFactoryBean<Any> = mock()

    @Test
    fun `name is prototype scope`() {
        assertThat(prototypeScopedFactoryBeanHandler.name).isEqualTo(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE)
    }

    @Test
    fun `prototype scope is always active`() {
        assertThat(prototypeScopedFactoryBeanHandler.isActive).isTrue()
    }

    @Test
    fun `getBean() lets FactoryBean produce the bean on invocation`() {
        val bean = Any()
        whenever(scopedFactoryBean.produceBean()).thenReturn(bean)

        val beanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)

        assertThat(beanInstance).isSameAs(bean)
    }

    @Test
    fun `getBean() produces a new bean instance every time`() {
        val firstBean = Any()
        val secondBean = Any()
        whenever(scopedFactoryBean.produceBean()).thenReturn(firstBean, secondBean)

        val firstBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)
        val secondBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)

        verify(scopedFactoryBean, Mockito.times(2)).produceBean()
        assertThat(firstBeanInstance).isSameAs(firstBean)
        assertThat(secondBeanInstance).isSameAs(secondBean)
    }
}
