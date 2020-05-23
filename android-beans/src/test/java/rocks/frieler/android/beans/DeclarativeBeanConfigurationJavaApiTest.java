package rocks.frieler.android.beans;

import org.junit.Test;

import java.util.List;

import kotlin.jvm.JvmClassMappingKt;
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean;
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean;
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
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
    public void testDeclarativeBeanConfigurationCanDefineBeansWithDependencies() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            @Override
            public void beans() {
                bean(DeclarativeBeanConfigurationJavaApiTest.class, (BeansProvider dependencies) -> {
                    dependencies.lookUpBean(DeclarativeBeanConfigurationJavaApiTest.class);
                    dependencies.lookUpBean("aCertainBean", DeclarativeBeanConfigurationJavaApiTest.class);
                    dependencies.lookUpOptionalBean(DeclarativeBeanConfigurationJavaApiTest.class);
                    dependencies.lookUpOptionalBean("aCertainBean", DeclarativeBeanConfigurationJavaApiTest.class);
                    dependencies.lookUpBeans(DeclarativeBeanConfigurationJavaApiTest.class);
                    return new DeclarativeBeanConfigurationJavaApiTest();
                });
            }
        };

        List<BeanDefinition<?>> beanDefinitions = beanConfiguration.getBeanDefinitions();

        assertThat(beanDefinitions.size(), is(equalTo(1)));
        assertThat(beanDefinitions.get(0).getName(), is(nullValue()));
    }

    @Test
    public void testDeclarativeBeanConfigurationCanDefineScopedBeans() {
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
    public void testDeclarativeBeanConfigurationCanDefineScopedBeansWithDependencies() {
        DeclarativeBeanConfiguration beanConfiguration = new DeclarativeBeanConfiguration() {
            @Override
            public void beans() {
                bean(
                		lazyInstantiated(DeclarativeBeanConfigurationJavaApiTest.class, (dependencies) -> {
                		    assertThat(dependencies, is(instanceOf(BeansProvider.class)));
                		    return new DeclarativeBeanConfigurationJavaApiTest();
                        })
                );

                bean(
                        prototype(DeclarativeBeanConfigurationJavaApiTest.class,  (dependencies) -> {
                            assertThat(dependencies, is(instanceOf(BeansProvider.class)));
                            return new DeclarativeBeanConfigurationJavaApiTest();
                        })
                );

                bean(
                        activityScoped(DeclarativeBeanConfigurationJavaApiTest.class,  (dependencies) -> {
                            assertThat(dependencies, is(instanceOf(BeansProvider.class)));
                            return new DeclarativeBeanConfigurationJavaApiTest();
                        })
                );
            }
        };

        List<BeanDefinition<?>> beanDefinitions = beanConfiguration.getBeanDefinitions();

        assertThat(beanDefinitions.get(0).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(SingletonScopedFactoryBean.class))));
        assertThat(beanDefinitions.get(1).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(PrototypeScopedFactoryBean.class))));
        assertThat(beanDefinitions.get(2).getType(), is(equalTo(JvmClassMappingKt.getKotlinClass(ActivityScopedFactoryBean.class))));
    }
}
