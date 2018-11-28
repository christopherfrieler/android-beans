package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BeansOfTypeConsumerTest {
    @SuppressWarnings("unchecked")
    private Consumer<BeansOfTypeConsumerTest> consumer = mock(Consumer.class);
    private BeansOfTypeConsumer<BeansOfTypeConsumerTest> beansOfTypeConsumer
            = new BeansOfTypeConsumer<>(BeansOfTypeConsumerTest.class, consumer);

    @Test
    public void testPostProcessIgnoresBeanNotOfSpecifiedType() {
        final Object originalBean = new Object();

        final Object processedBean = beansOfTypeConsumer.postProcessBean("bean", originalBean);

        verifyZeroInteractions(consumer);
        assertThat(processedBean, is(sameInstance(originalBean)));
    }

    @Test
    public void testPostProcessBeanConsumesBeanOfTypeAndReturnsIt() {
        final BeansOfTypeConsumerTest processedBean = beansOfTypeConsumer.postProcessBean("bean", this);

        verify(consumer).accept(this);
        assertThat(processedBean, is(sameInstance(this)));
    }
}