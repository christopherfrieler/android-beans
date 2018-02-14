package rocks.frieler.android.beans;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests the {@link BeanConfigurationsBeansCollector} in more complex situations where {@link BeanConfiguration}s
 * depend on each other.
 */
public class BeanConfigurationsBeansCollectorHandlesBeanDependenciesTest {
    private final BeanRegistry beanRegistry = new BeanRegistry(new ForegroundActivityHolder());
    private BeanConfigurationsBeansCollector beanConfigurationsBeansCollector = new BeanConfigurationsBeansCollector(beanRegistry);

    @Test(expected = BeanInstantiationException.class)
    public void testCollectBeansFailsOnCyclicDependency() {
        BeanConfiguration beanConfiguration1 = new BeanConfiguration1(new SingleBeanDependency<>(BeanConfiguration2.class));
        BeanConfiguration beanConfiguration2 = new BeanConfiguration2(new SingleBeanDependency<>(BeanConfiguration1.class));

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration1, beanConfiguration2));
    }

    @Test
    public void testCollectBeansResolvesSingleBeanDependenciesBetweenBeanConfigurations() {
        BeanConfiguration beanConfiguration1 = new BeanConfiguration1();
        BeanConfiguration beanConfiguration2 = new BeanConfiguration2(new SingleBeanDependency<>(BeanConfiguration1.class));
        BeanConfiguration beanConfiguration3 = new BeanConfiguration3(new SingleBeanDependency<>(BeanConfiguration2.class));

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(beanConfiguration2, beanConfiguration3, beanConfiguration1));

        assertThat(beanRegistry.lookUpBean(BeanConfiguration1.class), is(notNullValue()));
        assertThat(beanRegistry.lookUpBean(BeanConfiguration2.class), is(notNullValue()));
        assertThat(beanRegistry.lookUpBean(BeanConfiguration3.class), is(notNullValue()));
    }

    @Test
    public void testCollectBeansResolvesDependenciesBetweenBeanConfigurationsWhenOneRequestsAllBeansOfAType() {
        BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1 beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1 = new BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1();
        BeanConfiguration1 beanConfiguration1 = new BeanConfiguration1();
        BeanConfiguration beanConfiguration2 = new BeanConfiguration2();
        BeanConfiguration beanConfiguration3DependingOn2 = new BeanConfiguration3(new SingleBeanDependency<>(BeanConfiguration2.class));

        beanConfigurationsBeansCollector.collectBeans(Arrays.asList(
                beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1,
                beanConfiguration3DependingOn2,
                beanConfiguration1,
                beanConfiguration2));

        assertThat(beanRegistry.lookUpBean(BeanConfiguration1.class), is(notNullValue()));
        assertThat(beanRegistry.lookUpBean(BeanConfiguration2.class), is(notNullValue()));
        assertThat(beanRegistry.lookUpBean(BeanConfiguration3.class), is(notNullValue()));
        assertThat(beanRegistry.lookUpBean(BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1.class), is(notNullValue()));
        assertThat(beanConfigurationNeedingAllBeansOfTypeBeanConfiguration1.beanConfiguration1s, hasItem(beanConfiguration1));
    }


    private static abstract class BeanConfigurationWithDefinedDependencies extends BeanConfiguration {
        private final List<BeanDependency<?>> beanDependencies;

        private BeanConfigurationWithDefinedDependencies(BeanDependency<?>... beanDependencies) {
            this.beanDependencies = Arrays.asList(beanDependencies);
        }

        @Override
        protected List<BeanDependency<?>> getDependencies() {
            return beanDependencies;
        }
    }

    private static class BeanConfiguration1 extends BeanConfigurationWithDefinedDependencies {
        private BeanConfiguration1(BeanDependency<?>... beanDependencies) {
            super(beanDependencies);
        }

        @Override
        public void defineBeans(BeansCollector beansCollector) {
            beansCollector.defineBean(this);
        }
    }

    private static class BeanConfiguration2 extends BeanConfigurationWithDefinedDependencies {
        private BeanConfiguration2(BeanDependency<?>... beanDependencies) {
            super(beanDependencies);
        }

        @Override
        public void defineBeans(BeansCollector beansCollector) {
            beansCollector.defineBean(this);
        }
    }

    private static class BeanConfiguration3 extends BeanConfigurationWithDefinedDependencies {
        private BeanConfiguration3(BeanDependency<?>... beanDependencies) {
            super(beanDependencies);
        }

        @Override
        public void defineBeans(BeansCollector beansCollector) {
            beansCollector.defineBean(this);
        }
    }

    private static class BeanConfigurationNeedingAllBeansOfTypeBeanConfiguration1 extends BeanConfiguration {
        private BeanDependency<List<BeanConfiguration1>> beanConfiguration1sDependency = requireBeans(BeanConfiguration1.class);
        private List<BeanConfiguration1> beanConfiguration1s;

        @Override
        public void defineBeans(BeansCollector beansCollector) {
            beanConfiguration1s = beanConfiguration1sDependency.get();
            beansCollector.defineBean(this);
        }
    }
}