package rocks.frieler.android.beans.scopes.prototype

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
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration

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
        assertThat(producedBean).isSameAs(this)
    }

    @Test
    fun `prototype() provides a Pair of java-type and definition without dependencies for a PrototypeScopedFactoryBean`() {
        val dependencies = mock<BeansProvider>()
        val producerWithoutDependencies: () -> PrototypeScopedFactoryBeanTest = mock()
        whenever(producerWithoutDependencies.invoke()).thenReturn(this)

        val prototypeBeanDefinition = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest::class.java, producerWithoutDependencies)

        assertThat(prototypeBeanDefinition.first).isEqualTo(PrototypeScopedFactoryBean::class.java)
        val definedFactoryBean = prototypeBeanDefinition.second(dependencies)
        assertThat(definedFactoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
    }

    @Test
    fun `prototype() provides a Pair of java-type and definition for a PrototypeScopedFactoryBean`() {
        val dependencies: BeansProvider = mock()
        whenever(producer(dependencies)).thenReturn(this)

        val prototypeBeanDefinition = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest::class.java, producer)

        assertThat(prototypeBeanDefinition.first).isEqualTo(PrototypeScopedFactoryBean::class.java)
        val definedFactoryBean = prototypeBeanDefinition.second(dependencies)
        assertThat(definedFactoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
    }

    @Test
    fun `DeclarativeBeanConfiguration_prototypeBean() declares a PrototypeScopedFactoryBean`() {
        val dependencies: BeansProvider = mock()
        val beanConfiguration : DeclarativeBeanConfiguration = mock()
        val beanName = "aPrototypeBean"
        whenever(producer(dependencies)).thenReturn(this)

        beanConfiguration.prototypeBean(beanName) { producer() }

        val factoryBeanDefinitionCaptor = argumentCaptor<BeansProvider.() -> PrototypeScopedFactoryBean<*>>()
        verify(beanConfiguration).addBeanDefinition(eq(beanName), eq(PrototypeScopedFactoryBean::class), factoryBeanDefinitionCaptor.capture())
        val definedFactoryBean = factoryBeanDefinitionCaptor.firstValue(dependencies)
        assertThat(definedFactoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
    }
}
