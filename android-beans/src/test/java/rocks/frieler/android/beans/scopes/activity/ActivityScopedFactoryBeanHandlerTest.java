package rocks.frieler.android.beans.scopes.activity;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import androidx.fragment.app.FragmentActivity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ActivityScopedFactoryBeanHandlerTest {
    private ForegroundActivityHolder foregroundActivityHolder = mock(ForegroundActivityHolder.class);
    private ActivityScopedFactoryBeanHandler activityScopedFactoryBeanHandler = new ActivityScopedFactoryBeanHandler(foregroundActivityHolder);

    @Test
    public void testGetNameReturnsActivityScopeName() {
        assertThat(activityScopedFactoryBeanHandler.getName(), is(ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE));
    }

    @Test
    public void testScopeIsNotActiveWhenNoActivityIsInTheForeground() {
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(null);

        final boolean active = activityScopedFactoryBeanHandler.isActive();

        assertThat(active, is(false));
    }

    @Test
    public void testScopeIsNotActiveWhenTheActivityInTheForegroundIsNotAFragmentActivity() {
        final Activity activity = mock(Activity.class);
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(activity);

        final boolean active = activityScopedFactoryBeanHandler.isActive();

        assertThat(active, is(false));
    }

    @Test
    public void testScopeIsActiveWhenAFragmentActivityIsInTheForeground() {
        final Activity fragmentActivity = mock(FragmentActivity.class);
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(fragmentActivity);

        final boolean active = activityScopedFactoryBeanHandler.isActive();

        assertThat(active, is(true));
    }

    @Test
    public void testGetBeanReturnsBeanProducedByFactoryBean() {
        final Activity fragmentActivity = Robolectric.buildActivity(FragmentActivity.class).get();
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(fragmentActivity);

        @SuppressWarnings("unchecked")
        final ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> activityScopedFactoryBean = mock(ActivityScopedFactoryBean.class);
        when((activityScopedFactoryBean).produceBean()).thenReturn(this);

        final ActivityScopedFactoryBeanHandlerTest bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean);

        assertThat(bean, is(sameInstance(this)));
    }

    @Test
    public void testGetBeanReturnsBeanAlreadyPresentInActivityScope() {
        final Activity fragmentActivity = Robolectric.buildActivity(FragmentActivity.class).get();
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(fragmentActivity);

        @SuppressWarnings("unchecked")
        final ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> activityScopedFactoryBean = mock(ActivityScopedFactoryBean.class);
        when((activityScopedFactoryBean).produceBean()).thenReturn(this);

        final ActivityScopedFactoryBeanHandlerTest bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean);
        verify(activityScopedFactoryBean).produceBean();
        assertThat(bean, is(sameInstance(this)));
        //noinspection unchecked
        clearInvocations(activityScopedFactoryBean);

        final ActivityScopedFactoryBeanHandlerTest beanAgain = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean);
        verify(activityScopedFactoryBean, never()).produceBean();
        assertThat(beanAgain, is(sameInstance(bean)));
    }

    @Test
    public void testGetBeanSetsActivityOnActivityAwareBean() {
        final Activity fragmentActivity = Robolectric.buildActivity(FragmentActivity.class).get();
        when(foregroundActivityHolder.getCurrentActivity()).thenReturn(fragmentActivity);

        final ActivityAware activityAwareBean = mock(ActivityAware.class);
        @SuppressWarnings("unchecked")
        final ActivityScopedFactoryBean<ActivityAware> activityScopedFactoryBean = mock(ActivityScopedFactoryBean.class);
        when(activityScopedFactoryBean.produceBean()).thenReturn(activityAwareBean);

        final ActivityAware bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean);

        verify(activityAwareBean).setActivity(fragmentActivity);
        assertThat(bean, is(sameInstance(activityAwareBean)));
    }
}
