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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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

        List<BeanDefinition<?>> beanDefinitions = beanConfiguration.getBeanDefinitions();

        assertThat(beanDefinitions.size(), is(equalTo(2)));
        assertThat(beanDefinitions.get(0).getName(), is(nullValue()));
        assertThat(beanDefinitions.get(1).getName(), is("named_bean"));
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

        List<BeanDefinition<?>> beanDefinitions = beanConfiguration.getBeanDefinitions();

        assertThat(beanDefinitions.get(0).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(SingletonScopedFactoryBean.class))));
        assertThat(beanDefinitions.get(1).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(PrototypeScopedFactoryBean.class))));
        assertThat(beanDefinitions.get(2).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(ActivityScopedFactoryBean.class))));
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

        List<BeanDefinition<?>> beanDefinitions = beanConfiguration.getBeanDefinitions();

        assertThat(beanDefinitions.size(), is(2));
        final Object aBean = beanDefinitions.get(0).produceBean();
        assertThat(beanDefinitions.get(1).produceBean(), is(equalTo(aBean.toString())));
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
