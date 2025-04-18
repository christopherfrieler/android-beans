package rocks.frieler.android.beans.scopes.singleton

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeansProvider

class SingletonScopedFactoryBeanHandlerTest {

    private val singletonScopedFactoryBeanHandler = SingletonScopedFactoryBeanHandler()

    private val scopedFactoryBean: SingletonScopedFactoryBean<Any> = mock()

    @Test
    fun `name is singleton scope`() {
        assertThat(singletonScopedFactoryBeanHandler.name).isEqualTo(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE)
    }

    @Test
    fun `singleton scope is always active`() {
        assertThat(singletonScopedFactoryBeanHandler.isActive).isTrue()
    }

    @Test
    fun `getBean() lets the FactoryBean produce the bean on first invocation`() {
        val dependencies: BeansProvider = mock()
        val bean = Any()
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(bean)

        val beanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)

        assertThat(beanInstance).isSameInstanceAs(bean)
    }

    @Test
    fun `getBean() reuses singleton bean instance on every invocation`() {
        val dependencies: BeansProvider = mock()
        val bean = Any()
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(bean)

        val firstBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)
        val secondBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)

        verify(scopedFactoryBean, times(1)).produceBean(dependencies)
        assertThat(firstBeanInstance).isSameInstanceAs(bean)
        assertThat(secondBeanInstance).isSameInstanceAs(bean)
    }
}
