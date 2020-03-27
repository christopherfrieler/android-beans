package rocks.frieler.android.beans;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import java8.util.Optional;
import kotlin.jvm.JvmClassMappingKt;
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean;
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean;
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.activityScoped;
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.prototype;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazy;

public class BeanConfigurationJavaApiTest {

    @Test
    public void testBeanConfigurationCanDefineBeansWithAndWithoutName() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(@NotNull BeansCollector beansCollector) {
                beansCollector.defineBean(new Object());
                beansCollector.defineBean("named_bean", new Object());
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).defineBean(any());
        verify(beansCollector).defineBean(anyString(), any());
    }

    @Test
    public void testBeanConfigurationCanDefineScopedBeans() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(@NotNull BeansCollector beansCollector) {
                beansCollector.defineBean(lazy(BeanConfigurationJavaApiTest.class, BeanConfigurationJavaApiTest::new));
                beansCollector.defineBean(prototype(BeanConfigurationJavaApiTest.class, BeanConfigurationJavaApiTest::new));
                beansCollector.defineBean(activityScoped(BeanConfigurationJavaApiTest.class, BeanConfigurationJavaApiTest::new));
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).defineBean(any(SingletonScopedFactoryBean.class));
        verify(beansCollector).defineBean(any(PrototypeScopedFactoryBean.class));
        verify(beansCollector).defineBean(any(ActivityScopedFactoryBean.class));
    }

    @Test
    public void testBeanConfigurationCanRequireDependencies() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            private BeanDependency<BeanConfigurationJavaApiTest> beanDependency = requireBean(BeanConfigurationJavaApiTest.class);
            private BeanDependency<BeanConfigurationJavaApiTest> namedBeanDependency = requireBean("bean", BeanConfigurationJavaApiTest.class);
            private BeanDependency<Optional<BeanConfigurationJavaApiTest>> optionalBeanDependency = requireOptionalBean(BeanConfigurationJavaApiTest.class);
            private BeanDependency<List<BeanConfigurationJavaApiTest>> beansOfTypeDependency = requireBeans(BeanConfigurationJavaApiTest.class);

            @Override
            public void defineBeans(@NotNull BeansCollector beansCollector) {
                beanDependency.get();
                namedBeanDependency.get();
                optionalBeanDependency.get();
                beansOfTypeDependency.get();
            }
        };
        List<BeanDependency<?>> beanDependencies = beanConfiguration.getDependencies();

        assertThat(beanDependencies, hasItem(equalTo(new SingleBeanDependency<>(JvmClassMappingKt.getKotlinClass(BeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new SingleBeanDependency<>("bean", JvmClassMappingKt.getKotlinClass(BeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new OptionalSingleBeanDependency<>(JvmClassMappingKt.getKotlinClass(BeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new BeansOfTypeDependency<>(JvmClassMappingKt.getKotlinClass(BeanConfigurationJavaApiTest.class)))));
    }

    @Test
    public void testBeanConfigurationCanRegisterABeanPostProcessor() {
        BeanConfiguration beanConfiguration = new BeanConfiguration() {
            @Override
            public void defineBeans(@NotNull BeansCollector beansCollector) {
                beansCollector.registerBeanPostProcessor(mock(BeanPostProcessor.class));
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).registerBeanPostProcessor(any(BeanPostProcessor.class));
    }
}
