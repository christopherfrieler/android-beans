package rocks.frieler.android.beans.scopes.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Implementation of the {@link android.app.Application.ActivityLifecycleCallbacks} that holds the {@link Activity}
 * which is currently in the foreground (if any).
 */
public class ForegroundActivityHolder implements Application.ActivityLifecycleCallbacks {
    private Activity currentActivity = null;

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (this.currentActivity == activity) {
            this.currentActivity = null;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (this.currentActivity == activity) {
            this.currentActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}
