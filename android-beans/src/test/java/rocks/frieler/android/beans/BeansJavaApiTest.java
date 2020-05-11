package rocks.frieler.android.beans;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import kotlin.jvm.JvmClassMappingKt;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;


public class BeansJavaApiTest {
    @Before
    public void initializeBeans() {
        new Beans.Initializer()
                .collectBeans(Collections.singletonList(new BeanConfiguration() {
                    @NotNull
                    @Override
                    public List<BeanDefinition<?>> getBeanDefinitions() {
                        BeanDefinition<BeansJavaApiTest> aBeanDefinition = new BeanDefinition<>(
                                "beansJavaApiTest",
                                JvmClassMappingKt.getKotlinClass(BeansJavaApiTest.class),
                                (dependencies) -> BeansJavaApiTest.this);
                        return Collections.singletonList(aBeanDefinition);
                    }
                }))
                .initialize();
    }

    @Test
    public void testLookupSingleBeanByNameAndType() {
        BeansJavaApiTest bean = Beans.lookUpBean("beansJavaApiTest", BeansJavaApiTest.class);

        assertThat(bean, is(sameInstance(this)));
    }

    @Test
    public void testLookupSingleBeanByType() {
        BeansJavaApiTest bean = Beans.lookUpBean(BeansJavaApiTest.class);

        assertThat(bean, is(sameInstance(this)));
    }

    @Test
    public void testLookupOptionalBeanByNameAndType() {
        BeansJavaApiTest bean = Beans.lookUpOptionalBean("beansJavaApiTest", BeansJavaApiTest.class);

        assertThat(bean, is(sameInstance(this)));
    }

    @Test
    public void testLookupOptionalBeanByType() {
        BeansJavaApiTest bean = Beans.lookUpOptionalBean(BeansJavaApiTest.class);

        assertThat(bean, is(sameInstance(this)));
    }

    @Test
    public void testLookupAllBeansByType() {
        List<BeansJavaApiTest> beans = Beans.lookUpBeans(BeansJavaApiTest.class);

        assertThat(beans.size(), is(1));
        assertThat(beans, hasItem(this));
    }
}
