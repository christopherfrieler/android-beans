package rocks.frieler.android.beans.scopes.activity

import android.app.Activity
import assertk.assertThat
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.mockito.kotlin.mock
import org.junit.jupiter.api.Test

class ForegroundActivityHolderTest {
	private val foregroundActivityHolder = ForegroundActivityHolder()

	private val activity: Activity = mock()

	private val anotherActivity: Activity = mock()

	@Test
	fun `ForegroundActivityHolder holds created Activity`() {
		foregroundActivityHolder.onActivityCreated(activity, null)

		assertThat(foregroundActivityHolder.currentActivity).isSameInstanceAs(activity)
	}

	@Test
	fun `ForegroundActivityHolder holds started Activity`() {
		foregroundActivityHolder.onActivityStarted(activity)

		assertThat(foregroundActivityHolder.currentActivity).isSameInstanceAs(activity)
	}

	@Test
	fun `ForegroundActivityHolder holds resumed Activity`() {
		foregroundActivityHolder.onActivityResumed(activity)

		assertThat(foregroundActivityHolder.currentActivity).isSameInstanceAs(activity)
	}

	@Test
	fun `ForegroundActivityHolder resets Activity when paused`() {
		foregroundActivityHolder.onActivityResumed(activity)
		foregroundActivityHolder.onActivityPaused(activity)

		assertThat(foregroundActivityHolder.currentActivity).isNull()
	}

	@Test
	fun `ForegroundActivityHolder keeps Activity when another Activity is paused`() {
		foregroundActivityHolder.onActivityResumed(activity)
		foregroundActivityHolder.onActivityPaused(anotherActivity)

		assertThat(foregroundActivityHolder.currentActivity).isSameInstanceAs(activity)
	}

	@Test
	fun `ForegroundActivityHolder resets Activity when stopped`() {
		foregroundActivityHolder.onActivityResumed(activity)
		foregroundActivityHolder.onActivityStopped(activity)

		assertThat(foregroundActivityHolder.currentActivity).isNull()
	}

	@Test
	fun `ForegroundActivityHolder keeps Activity when another Activity is stopped`() {
		foregroundActivityHolder.onActivityResumed(activity)
		foregroundActivityHolder.onActivityStopped(anotherActivity)

		assertThat(foregroundActivityHolder.currentActivity).isSameInstanceAs(activity)
	}
}
