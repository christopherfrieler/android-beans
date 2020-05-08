package rocks.frieler.android.beans.scopes.singleton

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeansProvider

@RunWith(MockitoJUnitRunner::class)
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

        assertThat(beanInstance).isSameAs(bean)
    }

    @Test
    fun `getBean() reuses singleton bean instance on every invocation`() {
        val dependencies: BeansProvider = mock()
        val bean = Any()
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(bean)

        val firstBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)
        val secondBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean, dependencies)

        verify(scopedFactoryBean, times(1)).produceBean(dependencies)
        assertThat(firstBeanInstance).isSameAs(bean)
        assertThat(secondBeanInstance).isSameAs(bean)
    }
}
