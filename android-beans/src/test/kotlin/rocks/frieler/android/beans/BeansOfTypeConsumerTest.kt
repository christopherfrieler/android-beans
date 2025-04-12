package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.util.function.Consumer

class BeansOfTypeConsumerTest {
    private val consumer: (BeansOfTypeConsumerTest) -> Unit = mock()
    private val beansOfTypeConsumer = BeansOfTypeConsumer(BeansOfTypeConsumerTest::class, consumer)

    @Test
    fun `postProcessBean() ignores bean that is not of specified type`() {
        val originalBean = Any()

        val processedBean = beansOfTypeConsumer.postProcessBean("bean", originalBean)

        verifyNoInteractions(consumer)
        assertThat(processedBean).isSameInstanceAs(originalBean)
    }

    @Test
    fun `postProcessBean() consumes bean of specified type and returns it`() {
        val processedBean = beansOfTypeConsumer.postProcessBean("bean", this)

        verify(consumer)(this)
        assertThat(processedBean).isSameInstanceAs(this)
    }

    @Test
    fun `BeansOfTypeConsumer can be used with java-type and -Consumer`() {
        val consumer: Consumer<BeansOfTypeConsumerTest> = mock()

        val beansOfTypeConsumer = BeansOfTypeConsumer(BeansOfTypeConsumerTest::class.java, consumer)
        val processedBean = beansOfTypeConsumer.postProcessBean("bean", this)

        verify(consumer).accept(this)
        assertThat(processedBean).isSameInstanceAs(this)
    }
}
