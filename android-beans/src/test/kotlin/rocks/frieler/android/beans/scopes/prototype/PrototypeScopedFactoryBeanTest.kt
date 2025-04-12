package rocks.frieler.android.beans.scopes.prototype

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

class PrototypeScopedFactoryBeanTest {
    private val producer: BeansProvider.() -> PrototypeScopedFactoryBeanTest = mock()
    private val factoryBean = PrototypeScopedFactoryBean(PrototypeScopedFactoryBeanTest::class, producer)

    @Test
    fun `scope() is prototype scope`() {
        assertThat(factoryBean.scope).isEqualTo(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE)
    }

    @Test
    fun `beanType is the configured type`() {
        assertThat(factoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
    }

    @Test
    fun `produceBean() calls the configured producer`() {
        val dependencies: BeansProvider = mock()
        whenever(producer(dependencies)).thenReturn(this)

        val producedBean = factoryBean.produceBean(dependencies)

        verify(producer).invoke(dependencies)
        assertThat(producedBean).isSameInstanceAs(this)
    }

    @Test
    fun `prototype() without dependencies provides a BeanDefinition for a PrototypeScopedFactoryBean`() {
        val dependencies = mock<BeansProvider>()
        val producerWithoutDependencies: () -> PrototypeScopedFactoryBeanTest = mock()
        whenever(producerWithoutDependencies.invoke()).thenReturn(this)

        val prototypeBeanDefinition = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest::class.java, producerWithoutDependencies)

        assertThat(prototypeBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(prototypeBeanDefinition.getType()).isEqualTo(PrototypeScopedFactoryBean::class)
        assertPrototypeScopedFactoryBeanProducingThis(prototypeBeanDefinition.produceBean(dependencies), dependencies)
    }

    @Test
    fun `prototype() provides a BeanDefinition for a PrototypeScopedFactoryBean`() {
        val dependencies: BeansProvider = mock()
        whenever(producer(dependencies)).thenReturn(this)

        val prototypeBeanDefinition = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest::class.java, producer)

        assertThat(prototypeBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(prototypeBeanDefinition.getType()).isEqualTo(PrototypeScopedFactoryBean::class)
        assertPrototypeScopedFactoryBeanProducingThis(prototypeBeanDefinition.produceBean(dependencies), dependencies)
    }

    @Test
    fun `DeclarativeBeanConfiguration_prototypeBean() declares a PrototypeScopedFactoryBean`() {
        val dependencies: BeansProvider = mock()
        val beanConfiguration : DeclarativeBeanConfiguration = mock()
        val beanName = "aPrototypeBean"
        whenever(producer(dependencies)).thenReturn(this)

        beanConfiguration.prototypeBean(beanName) { producer() }

        val beanDefinitionCaptor = argumentCaptor<BeanDefinition<PrototypeScopedFactoryBean<PrototypeScopedFactoryBeanTest>>>()
        verify(beanConfiguration).addBeanDefinition(beanDefinitionCaptor.capture())
        val beanDefinition = beanDefinitionCaptor.firstValue
        assertThat(beanDefinition).isInstanceOf(ScopedBeanDefinition::class)
        assertThat(beanDefinition.getName()).isEqualTo(beanName)
        assertThat(beanDefinition.getType()).isEqualTo(PrototypeScopedFactoryBean::class)
        assertPrototypeScopedFactoryBeanProducingThis(beanDefinition.produceBean(dependencies), dependencies)
    }

    private fun assertPrototypeScopedFactoryBeanProducingThis(factoryBean: PrototypeScopedFactoryBean<*>, dependencies: BeansProvider) {
        assertThat(factoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(factoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(factoryBean.produceBean(dependencies)).isSameInstanceAs(this)
    }
}
