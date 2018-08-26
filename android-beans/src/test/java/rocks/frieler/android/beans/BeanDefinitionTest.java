package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BeanDefinitionTest {
    @Mock
    private BeansCollector beansCollector;

    @Test
    public void testBeanDefinitionWithoutNameSpecified() {
        BeanDefinition beanDefinition = new BeanDefinition() {
            @Override
            public Object bean() {
                return BeanDefinitionTest.this;
            }
        };
        beanDefinition.defineBeans(beansCollector);

        verify(beansCollector).defineBean(beanDefinition.bean());
    }

    @Test
    public void testBeanDefinitionWithNameSpecified() {
        final String beanName = "aBean";

        BeanDefinition beanDefinition = new BeanDefinition(beanName) {
            @Override
            public Object bean() {
                return BeanDefinitionTest.this;
            }
        };
        beanDefinition.defineBeans(beansCollector);

        verify(beansCollector).defineBean(beanName, beanDefinition.bean());
    }
}