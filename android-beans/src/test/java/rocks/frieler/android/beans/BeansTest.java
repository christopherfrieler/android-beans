package rocks.frieler.android.beans;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeansTest {
    @Mock
    private BeansProvider beansProvider;

    @Before
    public void setUp() {
        Beans.setBeans(beansProvider);
    }

    @Test
    public void testLookUpBeanDelegatesToTheBeanRegistry() {
        final String name = "bean";
        final Object bean = new Object();
        when(beansProvider.lookUpBean(name, Object.class)).thenReturn(bean);

        Object retrievedBean = Beans.lookUpBean(name, Object.class);

        assertThat(retrievedBean, is(sameInstance(bean)));
    }

    @Test
    public void testLookUpBeanByTypeDelegatesToTheBeanRegistry() {
        final Object bean = new Object();
        when(beansProvider.lookUpBean(Object.class)).thenReturn(bean);

        Object retrievedBean = Beans.lookUpBean(Object.class);

        assertThat(retrievedBean, is(sameInstance(bean)));
    }
}