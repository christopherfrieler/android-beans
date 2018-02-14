package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeansOfTypeDependencyTest {
    private BeansOfTypeDependency<BeansOfTypeDependencyTest> beansOfTypeDependency = new BeansOfTypeDependency<>(BeansOfTypeDependencyTest.class);

    @Mock
    private BeansProvider beansProvider;

    @Test
    public void testFulfillReturnsTrue() {
        boolean fulfilled = beansOfTypeDependency.fulfill(beansProvider);

        verifyZeroInteractions(beansProvider);
        assertThat(fulfilled, is(true));
    }

    @Test
    public void testGetObtainsBeansFromBeansProvider() {
        when(beansProvider.lookUpBeans(BeansOfTypeDependencyTest.class)).thenReturn(Collections.singletonList(this));

        beansOfTypeDependency.fulfill(beansProvider);
        List<BeansOfTypeDependencyTest> beans = beansOfTypeDependency.get();

        assertThat(beans, is(beansProvider.lookUpBeans(BeansOfTypeDependencyTest.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFailsWhenNotYetFulfilled() {
        beansOfTypeDependency.get();
    }
}