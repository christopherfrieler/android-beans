package rocks.frieler.android.beans.scopes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.frieler.android.beans.scopes.ScopedFactoryBeanDecorator.decorate;

@RunWith(MockitoJUnitRunner.class)
public class ScopedFactoryBeanDecoratorTest {
    @SuppressWarnings("unchecked")
    private ScopedFactoryBean<ScopedFactoryBeanDecoratorTest> scopedFactoryBean = mock(ScopedFactoryBean.class);
    private ScopedFactoryBeanDecorator<ScopedFactoryBeanDecoratorTest> decoratedFactoryBean = decorate(scopedFactoryBean);

    @Test
    public void testGetScopeDelegatesToOriginal() {
        when(scopedFactoryBean.getScope()).thenReturn("scope");

        final String scope = decoratedFactoryBean.getScope();

        assertThat(scope, is(scopedFactoryBean.getScope()));
    }

    @Test
    public void testGetBeanTypeDelegatesToOriginal() {
        when(scopedFactoryBean.getBeanType()).thenReturn(ScopedFactoryBeanDecoratorTest.class);

        final Class<?> beanType = decoratedFactoryBean.getBeanType();

        assertThat(beanType, is(equalTo(scopedFactoryBean.getBeanType())));
    }

    @Test
    public void testProduceBeanDelegatesToOriginal() {
        when(scopedFactoryBean.produceBean()).thenReturn(this);

        final ScopedFactoryBeanDecoratorTest bean = decoratedFactoryBean.produceBean();

        assertThat(bean, is(sameInstance(scopedFactoryBean.produceBean())));
    }

    @Test
    public void testProduceBeanAppliesPostProcessingWhenConfigured() {
        when(scopedFactoryBean.produceBean()).thenReturn(this);
        @SuppressWarnings("unchecked") final Function<ScopedFactoryBeanDecoratorTest, ScopedFactoryBeanDecoratorTest> postProcessing = mock(Function.class);
        when(postProcessing.apply(scopedFactoryBean.produceBean())).thenReturn(new ScopedFactoryBeanDecoratorTest());

        final ScopedFactoryBeanDecoratorTest bean = decoratedFactoryBean.withPostProcessing(postProcessing).produceBean();

        verify(postProcessing).apply(scopedFactoryBean.produceBean());
        assertThat(bean, is(sameInstance(postProcessing.apply(scopedFactoryBean.produceBean()))));
    }
}