package rocks.frieler.android.beans.scopes.activity

import android.app.Activity

/**
 * Interface for activity-scoped beans that need to be aware of the [Activity] they're scoped to.
 *
 * @author Christopher Frieler
 */
interface ActivityAware {
	/**
	 * Sets the [Activity], the bean is scoped to.
	 *
	 *
	 * The [Activity] is set again when the same bean is obtained for a new [Activity], e.g. after a
	 * configuration-change. At the end of the [Activity]'s lifecycle, this method is called with `null` to
	 * clear any references to the [Activity] and prevent memory-leaks.
	 *
	 * @param activity the [Activity], the bean is scoped to
	 */
	fun setActivity(activity: Activity?)
}
