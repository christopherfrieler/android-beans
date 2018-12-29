package rocks.frieler.android.beans.scopes.activity;

import android.app.Activity;

/**
 * Interface for activity-scoped beans that need to be aware of the {@link Activity} they're scoped to.
 */
public interface ActivityAware {
    /**
     * Sets the {@link Activity}, the bean is scoped to.
     * <p>
     * The {@link Activity} is set again when the same bean is obtained for a new {@link Activity}, e.g. after a
     * configuration-change. At the end of the {@link Activity}'s lifecycle, this method is called with {@code null} to
     * clear any references to the {@link Activity} and prevent memory-leaks.
     *
     * @param activity the {@link Activity}, the bean is scoped to
     */
    void setActivity(Activity activity);
}
