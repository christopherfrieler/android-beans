package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeansProvider
import kotlin.reflect.KClass

class ScopedFactoryBeanDecoratorTest {
    private val scopedFactoryBean: ScopedFactoryBean<ScopedFactoryBeanDecoratorTest> = mock()
    private val decoratedFactoryBean = scopedFactoryBean.decorate()

    @Test
    fun `scope delegates to original`() {
        whenever(scopedFactoryBean.scope).thenReturn("scope")

        val scope = decoratedFactoryBean.scope

        assertThat(scope).isEqualTo(scopedFactoryBean.scope)
    }

    @Test
    fun `beanType delegates to original`() {
        whenever(scopedFactoryBean.beanType).thenReturn(ScopedFactoryBeanDecoratorTest::class)

        val beanType: KClass<*> = decoratedFactoryBean.beanType

        assertThat(beanType).isEqualTo(scopedFactoryBean.beanType)
    }

    @Test
    fun `produceBean() delegates to the original`() {
        val dependencies: BeansProvider = mock()
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(this)

        val bean = decoratedFactoryBean.produceBean(dependencies)

        verify(scopedFactoryBean).produceBean(dependencies)
        assertThat(bean).isSameAs(this)
    }

    @Test
    fun `produceBean() applies postProcessing when configured`() {
        val dependencies: BeansProvider = mock()
        val bean = this
        whenever(scopedFactoryBean.produceBean(dependencies)).thenReturn(bean)
        val postProcessing: (ScopedFactoryBeanDecoratorTest) -> ScopedFactoryBeanDecoratorTest = mock()
        val beanAfterPostProcessing = ScopedFactoryBeanDecoratorTest()
        whenever(postProcessing.invoke(bean)).thenReturn(beanAfterPostProcessing)

        val finalBean = decoratedFactoryBean.withPostProcessing(postProcessing).produceBean(dependencies)

        inOrder(scopedFactoryBean, postProcessing) {
            verify(scopedFactoryBean).produceBean(dependencies)
            verify(postProcessing).invoke(bean)
        }
        assertThat(finalBean).isSameAs(beanAfterPostProcessing)
    }
}
