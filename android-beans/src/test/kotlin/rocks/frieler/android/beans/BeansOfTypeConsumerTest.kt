package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class BeansOfTypeConsumerTest {
    private val consumer: (BeansOfTypeConsumerTest) -> Unit = mock()
    private val beansOfTypeConsumer = BeansOfTypeConsumer(BeansOfTypeConsumerTest::class, consumer)

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

        verify(consumer)(this)
        assertThat(processedBean).isSameAs(this)
    }

    @Test
    fun `BeansOfTypeConsumer can be used with java-type and -Consumer`() {
        val consumer: Consumer<BeansOfTypeConsumerTest> = mock()

        val beansOfTypeConsumer = BeansOfTypeConsumer(BeansOfTypeConsumerTest::class.java, consumer)
        val processedBean = beansOfTypeConsumer.postProcessBean("bean", this)

        verify(consumer).accept(this)
        assertThat(processedBean).isSameAs(this)
    }
}
