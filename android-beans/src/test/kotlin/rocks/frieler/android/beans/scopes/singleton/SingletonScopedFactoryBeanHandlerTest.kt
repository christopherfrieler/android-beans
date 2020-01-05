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
    fun `getBean() lets FactoryBean produce the bean on first invocation`() {
        val bean = Any()
        whenever(scopedFactoryBean.produceBean()).thenReturn(bean)

        val beanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)

        assertThat(beanInstance).isSameAs(bean)
    }

    @Test
    fun `getBean() reuses singleton bean instance on every invocation`() {
        val bean = Any()
        whenever(scopedFactoryBean.produceBean()).thenReturn(bean)

        val firstBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)
        val secondBeanInstance = singletonScopedFactoryBeanHandler.getBean("bean", scopedFactoryBean)

        verify(scopedFactoryBean, times(1)).produceBean()
        assertThat(firstBeanInstance).isSameAs(bean)
        assertThat(secondBeanInstance).isSameAs(bean)
    }
}
