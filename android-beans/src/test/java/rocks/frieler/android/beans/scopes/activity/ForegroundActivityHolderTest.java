package rocks.frieler.android.beans.scopes.activity;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ForegroundActivityHolderTest {
    private ForegroundActivityHolder foregroundActivityHolder = new ForegroundActivityHolder();

    @Mock
    private Activity activity, anotherActivity;

    @Test
    public void testForegroundActivityHolderHoldsCreatedActivity() {
        foregroundActivityHolder.onActivityCreated(activity, null);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(sameInstance(activity)));
    }

    @Test
    public void testForegroundActivityHolderHoldsStartedActivity() {
        foregroundActivityHolder.onActivityStarted(activity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(sameInstance(activity)));
    }

    @Test
    public void testForegroundActivityHolderHoldsResumedActivity() {
        foregroundActivityHolder.onActivityResumed(activity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(sameInstance(activity)));
    }

    @Test
    public void testForegroundActivityHolderResetsActivityWhenPaused() {
        foregroundActivityHolder.onActivityResumed(activity);

        foregroundActivityHolder.onActivityPaused(activity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(nullValue()));
    }

    @Test
    public void testForegroundActivityHolderKeepsActivityWhenAnotherActivityIsPaused() {
        foregroundActivityHolder.onActivityResumed(activity);

        foregroundActivityHolder.onActivityPaused(anotherActivity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(sameInstance(activity)));
    }

    @Test
    public void testForegroundActivityHolderResetsActivityWhenStopped() {
        foregroundActivityHolder.onActivityResumed(activity);

        foregroundActivityHolder.onActivityStopped(activity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(nullValue()));
    }

    @Test
    public void testForegroundActivityHolderKeepsActivityWhenAnotherActivityIsStopped() {
        foregroundActivityHolder.onActivityResumed(activity);

        foregroundActivityHolder.onActivityStopped(anotherActivity);

        assertThat(foregroundActivityHolder.getCurrentActivity(), is(sameInstance(activity)));
    }
}