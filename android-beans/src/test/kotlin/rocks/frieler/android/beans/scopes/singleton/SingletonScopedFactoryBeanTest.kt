package rocks.frieler.android.beans.scopes.singleton

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.ScopedBeanDefinition
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.Companion.lazyInstantiated

class SingletonScopedFactoryBeanTest {
    private val producer: BeansProvider.() -> SingletonScopedFactoryBeanTest = mock()
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
        val dependencies = mock<BeansProvider>()
        whenever(producer(dependencies)).thenReturn(this)

        val producedBean = factoryBean.produceBean(dependencies)

        verify(producer).invoke(dependencies)
        assertThat(producedBean).isSameInstanceAs(this)
    }

    @Test
    fun `lazyInstantiated() without dependencies provides a BeanDefinition for a SingletonScopedFactoryBean`() {
        val dependencies = mock<BeansProvider>()
        val producerWithoutDependencies: () -> SingletonScopedFactoryBeanTest = mock()
        whenever(producerWithoutDependencies()).thenReturn(this)

        val lazyInstantiatedBeanDefinition = lazyInstantiated(SingletonScopedFactoryBeanTest::class.java, producerWithoutDependencies)

        assertThat(lazyInstantiatedBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(lazyInstantiatedBeanDefinition.getType()).isEqualTo(SingletonScopedFactoryBean::class)
        assertSingletonScopedFactoryBeanProducingThis(lazyInstantiatedBeanDefinition.produceBean(dependencies), dependencies)
    }

    @Test
    fun `lazyInstantiated() provides a BeanDefinition for a SingletonScopedFactoryBean`() {
        val dependencies = mock<BeansProvider>()
        whenever(producer(dependencies)).thenReturn(this)

        val lazyInstantiatedBeanDefinition = lazyInstantiated(SingletonScopedFactoryBeanTest::class.java, producer)

        assertThat(lazyInstantiatedBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(lazyInstantiatedBeanDefinition.getType()).isEqualTo(SingletonScopedFactoryBean::class)
        assertSingletonScopedFactoryBeanProducingThis(lazyInstantiatedBeanDefinition.produceBean(dependencies), dependencies)
    }

    @Test
    fun `DeclarativeBeanConfiguration_lazyInstantiatedBean() declares a SingletonScopedFactoryBean`() {
        val dependencies = mock<BeansProvider>()
        val beanConfiguration : DeclarativeBeanConfiguration = mock()
        val beanName = "aLazyInstantiatedBean"
        whenever(producer(dependencies)).thenReturn(this)

        beanConfiguration.lazyInstantiatedBean(beanName) { producer() }

        val beanDefinitionCaptor = argumentCaptor<BeanDefinition<SingletonScopedFactoryBean<SingletonScopedFactoryBeanTest>>>()
        verify(beanConfiguration).addBeanDefinition(beanDefinitionCaptor.capture())
        val beanDefinition = beanDefinitionCaptor.firstValue
        assertThat(beanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(beanDefinition.getName()).isEqualTo(beanName)
        assertThat(beanDefinition.getType()).isEqualTo(SingletonScopedFactoryBean::class)
        assertSingletonScopedFactoryBeanProducingThis(beanDefinition.produceBean(dependencies), dependencies)
    }

    private fun assertSingletonScopedFactoryBeanProducingThis(factoryBean: SingletonScopedFactoryBean<*>, dependencies: BeansProvider) {
        assertThat(factoryBean).isInstanceOf(SingletonScopedFactoryBean::class)
        assertThat(factoryBean.beanType).isEqualTo(SingletonScopedFactoryBeanTest::class)
        assertThat(factoryBean.produceBean(dependencies)).isSameInstanceAs(this)
    }
}
