package rocks.frieler.android.beans.scopes.singleton

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.Companion.lazyInstantiated

class SingletonScopedFactoryBeanTest {
    private val producer: () -> SingletonScopedFactoryBeanTest = mock()
    private val factoryBean = SingletonScopedFactoryBean(SingletonScopedFactoryBeanTest::class, producer)

    @Test
    fun `scope() is singleton scope`() {
        assertThat(factoryBean.scope).isEqualTo(SingletonScopedFactoryBeanHandler.SINGLETON_SCOPE)
    }

    @Test
    fun `beanType is the configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(SingletonScopedFactoryBeanTest::class)
    }

    @Test
    fun `produceBean() calls the configured producer`() {
        whenever(producer()).thenReturn(this)

        val producedBean = factoryBean.produceBean()

        verify(producer)()
        assertThat(producedBean).isSameAs(this)
    }

    @Test
    fun `lazyInstantiated() provides a Pair of java-type and definition for a SingletonScopedFactoryBean`() {
        whenever(producer()).thenReturn(this)

        val lazyInstantiatedBeanDefinition = lazyInstantiated(SingletonScopedFactoryBeanTest::class.java, producer)

        assertThat(lazyInstantiatedBeanDefinition.first).isEqualTo(SingletonScopedFactoryBean::class.java)
        val definedFactoryBean = lazyInstantiatedBeanDefinition.second()
        assertThat(definedFactoryBean).isInstanceOf(SingletonScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(SingletonScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean()).isSameAs(this)
    }

    @Test
    fun `DeclarativeBeanConfiguration_lazyInstantiatedBean() declares a SingletonScopedFactoryBean`() {
        val beanConfiguration : DeclarativeBeanConfiguration = mock()
        val beanName = "aLazyInstantiatedBean"
        whenever(producer()).thenReturn(this)

        beanConfiguration.lazyInstantiatedBean(beanName) { producer() }

        val factoryBeanDefinitionCaptor = argumentCaptor<() -> SingletonScopedFactoryBean<*>>()
        verify(beanConfiguration).addBeanDefinition(eq(beanName), eq(SingletonScopedFactoryBean::class), factoryBeanDefinitionCaptor.capture())
        val definedFactoryBean = factoryBeanDefinitionCaptor.firstValue()
        assertThat(definedFactoryBean).isInstanceOf(SingletonScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(SingletonScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean()).isSameAs(this)
    }
}
