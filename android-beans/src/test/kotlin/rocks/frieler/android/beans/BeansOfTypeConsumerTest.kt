package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import java8.util.function.Consumer
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BeansOfTypeConsumerTest {
    private val consumer: Consumer<BeansOfTypeConsumerTest> = mock()
    private val beansOfTypeConsumer = BeansOfTypeConsumer(BeansOfTypeConsumerTest::class.java, consumer)

    @Test
    fun `postProcessBean() ignores bean that is not of specified type`() {
        val originalBean = Any()

        val processedBean = beansOfTypeConsumer.postProcessBean("bean", originalBean)

        verifyZeroInteractions(consumer)
        assertThat(processedBean).isSameAs(originalBean)
    }

    @Test
    fun `postProcessBean() consumes bean of specified type and returns it`() {
        val processedBean = beansOfTypeConsumer.postProcessBean("bean", this)

        verify(consumer).accept(this)
        assertThat(processedBean).isSameAs(this)
    }
}