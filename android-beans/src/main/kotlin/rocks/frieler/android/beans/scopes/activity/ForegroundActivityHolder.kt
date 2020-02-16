package rocks.frieler.android.beans.scopes.activity

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 * Implementation of the [android.app.Application.ActivityLifecycleCallbacks] that holds the [Activity]
 * which is currently in the foreground (if any).
 */
class ForegroundActivityHolder : ActivityLifecycleCallbacks {
	var currentActivity: Activity? = null
		private set

	override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
		currentActivity = activity
	}

	override fun onActivityStarted(activity: Activity) {
		currentActivity = activity
	}

	override fun onActivityResumed(activity: Activity) {
		currentActivity = activity
	}

	override fun onActivityPaused(activity: Activity) {
		if (currentActivity === activity) {
			currentActivity = null
		}
	}

	override fun onActivityStopped(activity: Activity) {
		if (currentActivity === activity) {
			currentActivity = null
		}
	}

	override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
	override fun onActivityDestroyed(activity: Activity) {}
}
