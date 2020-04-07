package rocks.frieler.android.beans;

import org.junit.Test;

import java.util.List;

import java8.util.Optional;
import kotlin.jvm.JvmClassMappingKt;
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean;
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean;
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.activityScoped;
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.prototype;
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazyInstantiated;

public class DeclarativeBeanConfigurationJavaApiTest {

    @Test
    public void testDeclarativeBeanConfigurationCanDefineBeansWithAndWithoutName() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            @Override
            public void beans() {
                bean(DeclarativeBeanConfigurationJavaApiTest.class, DeclarativeBeanConfigurationJavaApiTest::new);
                bean("named_bean", Object.class, Object::new);
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).defineBean(any(DeclarativeBeanConfigurationJavaApiTest.class));
        verify(beansCollector).defineBean(anyString(), any());
    }

    @Test
    public void testBeanConfigurationCanDefineScopedBeans() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            @Override
            public void beans() {
                bean(
                		lazyInstantiated(DeclarativeBeanConfigurationJavaApiTest.class, DeclarativeBeanConfigurationJavaApiTest::new)
                );

                bean(
                        prototype(DeclarativeBeanConfigurationJavaApiTest.class, DeclarativeBeanConfigurationJavaApiTest::new)
                );

                bean(
                        activityScoped(DeclarativeBeanConfigurationJavaApiTest.class, DeclarativeBeanConfigurationJavaApiTest::new)
                );
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).defineBean(any(SingletonScopedFactoryBean.class));
        verify(beansCollector).defineBean(any(PrototypeScopedFactoryBean.class));
        verify(beansCollector).defineBean(any(ActivityScopedFactoryBean.class));
    }

    @Test
    public void testBeanConfigurationCanUseBeansDefinedEarlierInThisBeanConfiguration() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            @Override
            public void beans() {
                BeanReference<DeclarativeBeanConfigurationJavaApiTest> aBean = bean(DeclarativeBeanConfigurationJavaApiTest.class, DeclarativeBeanConfigurationJavaApiTest::new);
                bean(String.class, () -> aBean.use().toString());
            }
        };

        BeansCollector beansCollector = mock(BeansCollector.class);
        beanConfiguration.defineBeans(beansCollector);

        verify(beansCollector).defineBean(any(DeclarativeBeanConfigurationJavaApiTest.class));
        verify(beansCollector).defineBean(any(String.class));
    }

    @Test
    public void testBeanConfigurationCanRequireDependencies() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            private BeanDependency<DeclarativeBeanConfigurationJavaApiTest> beanDependency = requireBean(DeclarativeBeanConfigurationJavaApiTest.class);
            private BeanDependency<DeclarativeBeanConfigurationJavaApiTest> namedBeanDependency = requireBean("bean", DeclarativeBeanConfigurationJavaApiTest.class);
            private BeanDependency<Optional<DeclarativeBeanConfigurationJavaApiTest>> optionalBeanDependency = requireOptionalBean(DeclarativeBeanConfigurationJavaApiTest.class);
            private BeanDependency<List<DeclarativeBeanConfigurationJavaApiTest>> beansOfTypeDependency = requireBeans(DeclarativeBeanConfigurationJavaApiTest.class);

            @Override
            public void beans() {
                beanDependency.get();
                namedBeanDependency.get();
                optionalBeanDependency.get();
                beansOfTypeDependency.get();
            }
        };
        List<BeanDependency<?>> beanDependencies = beanConfiguration.getDependencies();

        assertThat(beanDependencies, hasItem(equalTo(new SingleBeanDependency<>(JvmClassMappingKt.getKotlinClass(DeclarativeBeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new SingleBeanDependency<>("bean", JvmClassMappingKt.getKotlinClass(DeclarativeBeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new OptionalSingleBeanDependency<>(JvmClassMappingKt.getKotlinClass(DeclarativeBeanConfigurationJavaApiTest.class)))));
        assertThat(beanDependencies, hasItem(equalTo(new BeansOfTypeDependency<>(JvmClassMappingKt.getKotlinClass(DeclarativeBeanConfigurationJavaApiTest.class)))));
    }
}
