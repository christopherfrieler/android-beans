package rocks.frieler.android.beans;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java8.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OptionalSingleBeanDependencyTest {
    @Mock
    private BeansProvider beansProvider;

    @Test
    public void testFulfillReturnsTrue() {
        boolean fulfilled = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class).fulfill(beansProvider);

        verifyZeroInteractions(beansProvider);
        assertThat(fulfilled, is(true));
    }

    @Test
    public void testGetObtainsExistingBeanByTypeFromBeansProvider() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> bean = optionalSingleBeanDependency.get();

        assertThat(bean.isPresent(), is(true));
        assertThat(bean.get(), is(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)));
    }

    @Test
    public void testGetObtainsExistingBeanByNameAndTypeFromBeansProvider() {
        when(beansProvider.lookUpBean("bean", OptionalSingleBeanDependencyTest.class)).thenReturn(this);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>("bean", OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> bean = optionalSingleBeanDependency.get();

        assertThat(bean.isPresent(), is(true));
        assertThat(bean.get(), is(beansProvider.lookUpBean("bean", OptionalSingleBeanDependencyTest.class)));
    }

    @Test
    public void testGetReturnsEmptyOptionalWhenWantedBeanIsNOtAvailable() {
        when(beansProvider.lookUpBean(OptionalSingleBeanDependencyTest.class)).thenReturn(null);

        OptionalSingleBeanDependency<OptionalSingleBeanDependencyTest> optionalSingleBeanDependency
                = new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class);
        optionalSingleBeanDependency.fulfill(beansProvider);
        Optional<OptionalSingleBeanDependencyTest> bean = optionalSingleBeanDependency.get();

        assertThat(bean.isEmpty(), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFailsWhenNotYetFulfilled() {
        new OptionalSingleBeanDependency<>(OptionalSingleBeanDependencyTest.class).get();
    }
}