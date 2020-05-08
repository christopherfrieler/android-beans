package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java8.util.function.Function
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.BeansProvider
import kotlin.reflect.KClass

@RunWith(MockitoJUnitRunner::class)
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
        val postProcessing: Function<ScopedFactoryBeanDecoratorTest, ScopedFactoryBeanDecoratorTest> = mock()
        val beanAfterPostProcessing = ScopedFactoryBeanDecoratorTest()
        whenever(postProcessing.apply(bean)).thenReturn(beanAfterPostProcessing)

        val finalBean = decoratedFactoryBean.withPostProcessing(postProcessing).produceBean(dependencies)

        inOrder(scopedFactoryBean, postProcessing) {
            verify(scopedFactoryBean).produceBean(dependencies)
            verify(postProcessing).apply(bean)
        }
        assertThat(finalBean).isSameAs(beanAfterPostProcessing)
    }
}
