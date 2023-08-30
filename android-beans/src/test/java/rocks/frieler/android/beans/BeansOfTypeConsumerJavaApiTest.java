package rocks.frieler.android.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import java.util.function.Consumer;

class BeansOfTypeConsumerJavaApiTest {
	@Test
	void BeansOfTypeConsumerCanBeUsedWithJavaTypeAndConsumer() {
		@SuppressWarnings("unchecked") Consumer<BeansOfTypeConsumerJavaApiTest> consumer = mock(Consumer.class);

		BeansOfTypeConsumer<BeansOfTypeConsumerJavaApiTest> beansOfTypeConsumer = new BeansOfTypeConsumer<>(BeansOfTypeConsumerJavaApiTest.class, consumer);
		BeansOfTypeConsumerJavaApiTest processedBean = beansOfTypeConsumer.postProcessBean("bean", this);

		verify(consumer).accept(this);
		assertThat(processedBean, is(sameInstance(this)));
	}
}
