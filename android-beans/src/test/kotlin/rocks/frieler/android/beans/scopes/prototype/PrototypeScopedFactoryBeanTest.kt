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
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.DeclarativeBeanConfiguration

class PrototypeScopedFactoryBeanTest {
    private val producer: () -> PrototypeScopedFactoryBeanTest = mock()
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
        whenever(producer()).thenReturn(this)

        val producedBean = factoryBean.produceBean()

        verify(producer)()
        assertThat(producedBean).isSameAs(this)
    }

    @Test
    fun `prototype() provides a Pair of java-type and definition for a PrototypeScopedFactoryBean`() {
        whenever(producer()).thenReturn(this)

        val prototypeBeanDefinition = PrototypeScopedFactoryBean.prototype(PrototypeScopedFactoryBeanTest::class.java, producer)

        assertThat(prototypeBeanDefinition.first).isEqualTo(PrototypeScopedFactoryBean::class.java)
        val definedFactoryBean = prototypeBeanDefinition.second()
        assertThat(definedFactoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean()).isSameAs(this)
    }

    @Test
    fun `DeclarativeBeanConfiguration_prototypeBean() declares a PrototypeScopedFactoryBean`() {
        val beanConfiguration : DeclarativeBeanConfiguration = mock()
        val beanName = "aPrototypeBean"
        whenever(producer()).thenReturn(this)

        beanConfiguration.prototypeBean(beanName) { producer() }

        val factoryBeanDefinitionCaptor = argumentCaptor<() -> PrototypeScopedFactoryBean<*>>()
        verify(beanConfiguration).addBeanDefinition(eq(beanName), eq(PrototypeScopedFactoryBean::class), factoryBeanDefinitionCaptor.capture())
        val definedFactoryBean = factoryBeanDefinitionCaptor.firstValue()
        assertThat(definedFactoryBean).isInstanceOf(PrototypeScopedFactoryBean::class)
        assertThat(definedFactoryBean.beanType).isEqualTo(PrototypeScopedFactoryBeanTest::class)
        assertThat(definedFactoryBean.produceBean()).isSameAs(this)
    }

}
