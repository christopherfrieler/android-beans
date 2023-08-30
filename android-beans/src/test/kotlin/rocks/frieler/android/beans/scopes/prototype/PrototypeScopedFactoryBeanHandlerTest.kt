package rocks.frieler.android.beans.scopes.prototype

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeansProvider

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
        val dependencies: BeansProvider = mock()
        val bean = Any()
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(bean)

        val beanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)

        assertThat(beanInstance).isSameAs(bean)
    }

    @Test
    fun `getBean() produces a new bean instance every time`() {
        val beansProvider: BeansProvider = mock()
        val firstBean = Any()
        val secondBean = Any()
        whenever(scopedFactoryBean.produceBean(beansProvider)).thenReturn(firstBean, secondBean)

        val firstBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, beansProvider)
        val secondBeanInstance = prototypeScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, beansProvider)

        verify(scopedFactoryBean, Mockito.times(2)).produceBean(beansProvider)
        assertThat(firstBeanInstance).isSameAs(firstBean)
        assertThat(secondBeanInstance).isSameAs(secondBean)
    }
}
