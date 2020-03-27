package rocks.frieler.android.beans.scopes

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java8.util.function.Function
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanDecorator.Companion.decorate
import kotlin.reflect.KClass

@RunWith(MockitoJUnitRunner::class)
class ScopedFactoryBeanDecoratorTest {
    private val scopedFactoryBean: ScopedFactoryBean<ScopedFactoryBeanDecoratorTest> = mock()
    private val decoratedFactoryBean = decorate(scopedFactoryBean)

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
    fun `produceBean() delegates to original`() {
        whenever(scopedFactoryBean.produceBean()).thenReturn(this)

        val bean = decoratedFactoryBean.produceBean()

        assertThat(bean).isSameAs(scopedFactoryBean.produceBean())
    }

    @Test
    fun `produceBean() applies postProcessing when configured`() {
        whenever(scopedFactoryBean.produceBean()).thenReturn(this)
        val postProcessing: Function<ScopedFactoryBeanDecoratorTest, ScopedFactoryBeanDecoratorTest> = mock()
        whenever(postProcessing.apply(scopedFactoryBean.produceBean())).thenReturn(ScopedFactoryBeanDecoratorTest())

        val bean = decoratedFactoryBean.withPostProcessing(postProcessing).produceBean()

        verify(postProcessing).apply(scopedFactoryBean.produceBean())
        assertThat(bean).isSameAs(postProcessing.apply(scopedFactoryBean.produceBean()))
    }
}
