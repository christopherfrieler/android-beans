package rocks.frieler.android.beans.scopes.activity

import android.app.Activity
import androidx.activity.ComponentActivity
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityScopedFactoryBeanHandlerTest {
	private val foregroundActivityHolder : ForegroundActivityHolder = mock()
	private val activityScopedFactoryBeanHandler = ActivityScopedFactoryBeanHandler(foregroundActivityHolder)

	@Test
	fun `name is Activity-scope`() {
		assertThat(activityScopedFactoryBeanHandler.name).isEqualTo(ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE)
	}

	@Test
	fun `ActivityScope is not active when no Activity is in the foreground`() {
		whenever(foregroundActivityHolder.currentActivity).thenReturn(null)

		val active = activityScopedFactoryBeanHandler.isActive

		assertThat(active).isFalse()
	}

	@Test
	fun `ActivityScope is not active when the Activity in the foreground is not a FragmentActivity`() {
		val activity: Activity = mock()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(activity)

		val active = activityScopedFactoryBeanHandler.isActive

		assertThat(active).isFalse()
	}

	@Test
	fun `ActivityScope is active when a ComponentActivity is in the foreground`() {
		val componentActivity: ComponentActivity = mock()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)

		val active = activityScopedFactoryBeanHandler.isActive

		assertThat(active).isTrue()
	}

	@Test
	fun `getBean() returns bean produced by ActivityScopedFactoryBean`() {
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> = mock()
		whenever(activityScopedFactoryBean.produceBean()).thenReturn(this)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `getBean() returns bean already present in Activity-scope`() {
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> = mock()
		whenever(activityScopedFactoryBean.produceBean()).thenReturn(this)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean)

		verify(activityScopedFactoryBean).produceBean()
		assertThat(bean).isSameAs(this)
		clearInvocations(activityScopedFactoryBean)

		val beanAgain = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean)

		verify(activityScopedFactoryBean, never()).produceBean()
		assertThat(beanAgain).isSameAs(bean)
	}

	@Test
	fun `getBean() sets Activity on ActivityAware bean`() {
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityAwareBean: ActivityAware = mock()
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityAware> = mock()
		whenever(activityScopedFactoryBean.produceBean()).thenReturn(activityAwareBean)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean)

		verify(activityAwareBean).setActivity(componentActivity)
		assertThat(bean).isSameAs(activityAwareBean)
	}
}
