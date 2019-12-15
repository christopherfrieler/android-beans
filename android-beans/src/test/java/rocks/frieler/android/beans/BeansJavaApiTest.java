package rocks.frieler.android.beans;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;


public class BeansJavaApiTest {
    @Before
    public void initializeBeans() {
        new Beans.Initializer()
                .collectBeans(Collections.singletonList(new BeanConfiguration() {
                    @Override
                    public void defineBeans(BeansCollector beansCollector) {
                        beansCollector.defineBean("beansJavaApiTest", BeansJavaApiTest.this);
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
    public void testLookupAllBeansByType() {
        List<BeansJavaApiTest> beans = Beans.lookUpBeans(BeansJavaApiTest.class);

        assertThat(beans.size(), is(1));
        assertThat(beans, hasItem(this));
    }
}
