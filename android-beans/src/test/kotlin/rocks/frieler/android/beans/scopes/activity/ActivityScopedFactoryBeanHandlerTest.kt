package rocks.frieler.android.beans.scopes.activity

import android.app.Activity
import androidx.activity.ComponentActivity
import assertk.assertThat
import assertk.assertions.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import rocks.frieler.android.beans.BeansProvider

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
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
		val dependencies: BeansProvider = mock()
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> = mock()
		whenever(activityScopedFactoryBean.produceBean(dependencies)).thenReturn(this)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean, dependencies)

		assertThat(bean).isSameInstanceAs(this)
	}

	@Test
	fun `getBean() returns bean already present in Activity-scope`() {
		val dependencies: BeansProvider = mock()
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityScopedFactoryBeanHandlerTest> = mock()
		whenever(activityScopedFactoryBean.produceBean(dependencies)).thenReturn(this)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean, dependencies)

		verify(activityScopedFactoryBean).produceBean(dependencies)
		assertThat(bean).isSameInstanceAs(this)
		clearInvocations(activityScopedFactoryBean)

		val beanAgain = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean, dependencies)

		verify(activityScopedFactoryBean, never()).produceBean(dependencies)
		assertThat(beanAgain).isSameInstanceAs(bean)
	}

	@Test
	fun `getBean() sets Activity on ActivityAware bean`() {
		val dependencies: BeansProvider = mock()
		val componentActivity: Activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
		whenever(foregroundActivityHolder.currentActivity).thenReturn(componentActivity)
		val activityAwareBean: ActivityAware = mock()
		val activityScopedFactoryBean: ActivityScopedFactoryBean<ActivityAware> = mock()
		whenever(activityScopedFactoryBean.produceBean(dependencies)).thenReturn(activityAwareBean)

		val bean = activityScopedFactoryBeanHandler.getBean("bean", activityScopedFactoryBean, dependencies)

		verify(activityAwareBean).setActivity(componentActivity)
		assertThat(bean).isSameInstanceAs(activityAwareBean)
	}
}
