package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import java8.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanConfigurationTest {
    @Mock
    private BeansProvider beansProvider;

    @Test
    public void testBeanConfigurationReturnsNoBeanDependenciesByDefault() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(BeansCollector beansCollector) {}
        };
        List<BeanDependency<?>> dependencies = beanConfiguration.getDependencies();

        assertThat(dependencies, is(empty()));
    }

    @Test
    public void testBeanConfigurationRequiresBeanByNameAndType() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(BeansCollector beansCollector) {}
        };

        BeanDependency<BeanConfigurationTest> dependency = beanConfiguration.requireBean("name", BeanConfigurationTest.class);
        List<BeanDependency<?>> dependencies = beanConfiguration.getDependencies();

        assertThat(dependencies, hasItem(dependency));
    }

    @Test
    public void testBeanConfigurationRequiresBeanByType() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(BeansCollector beansCollector) {}
        };

        BeanDependency<BeanConfigurationTest> dependency = beanConfiguration.requireBean(BeanConfigurationTest.class);
        List<BeanDependency<?>> dependencies = beanConfiguration.getDependencies();

        assertThat(dependencies, hasItem(dependency));
    }

    @Test
    public void testBeanConfigurationRequiresOptionalBeanByType() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(BeansCollector beansCollector) {}
        };

        BeanDependency<Optional<BeanConfigurationTest>> dependency = beanConfiguration.requireOptionalBean(BeanConfigurationTest.class);
        List<BeanDependency<?>> dependencies = beanConfiguration.getDependencies();

        assertThat(dependencies, hasItem(dependency));
    }

    @Test
    public void testBeanConfigurationRequiresBeansByType() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(BeansCollector beansCollector) {}
        };

        BeanDependency<List<BeanConfigurationTest>> dependency = beanConfiguration.requireBeans(BeanConfigurationTest.class);
        List<BeanDependency<?>> dependencies = beanConfiguration.getDependencies();

        assertThat(dependencies, hasItem(dependency));
    }

    @Test
    public void testBeanConfigurationIsReadyToDefineBeansWithoutDependencies() {
        BeanConfiguration beanConfiguration = new BeanConfigurationWithDefinedDependencies();

        assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider), is(true));
    }

    @Test
    public void testBeanConfigurationIsReadyToDefineBeansWhenAllDependenciesAreFulfilled() {
        BeanDependency dependency1 = mock(BeanDependency.class);
        when(dependency1.fulfill(beansProvider)).thenReturn(true);
        BeanDependency dependency2 = mock(BeanDependency.class);
        when(dependency2.fulfill(beansProvider)).thenReturn(true);

        BeanConfiguration beanConfiguration = new BeanConfigurationWithDefinedDependencies(dependency1, dependency2);

        assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider), is(true));
    }

    @Test
    public void testBeanConfigurationIsNotReadyToDefineBeansWhenADependenciesIsNotFulfilled() {
        BeanDependency dependency = mock(BeanDependency.class);
        when(dependency.fulfill(beansProvider)).thenReturn(false);

        BeanConfiguration beanConfiguration = new BeanConfigurationWithDefinedDependencies(dependency);

        assertThat(beanConfiguration.isReadyToDefineBeans(beansProvider), is(false));
    }

    private static class BeanConfigurationWithDefinedDependencies extends BeanConfiguration {
        private final List<BeanDependency<?>> beanDependencies;

        private BeanConfigurationWithDefinedDependencies(BeanDependency<?>... beanDependencies) {
            this.beanDependencies = Arrays.asList(beanDependencies);
        }

        @Override
        protected List<BeanDependency<?>> getDependencies() {
            return beanDependencies;
        }

        @Override
        public void defineBeans(BeansCollector beansCollector) {}
    }
}