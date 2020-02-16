package rocks.frieler.android.beans.scopes.activity

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.Companion.ACTIVITY_SCOPE

/**
 * [ScopedFactoryBeanHandler] for the [ACTIVITY_SCOPE]-scope.
 *
 *
 * It provides a bean-instance per [android.app.Activity], always scoped to the
 * [foreground-activity][ForegroundActivityHolder.currentActivity]. Due to the limitation of the [ViewModelProviders]
 * this only works for subtypes of [FragmentActivity] such as the `android.support.v7.app.AppCompatActivity`.
 *
 *
 * The [ActivityScopedFactoryBeanHandler] supports the [ActivityAware]-interface.
 */
class ActivityScopedFactoryBeanHandler(private val foregroundActivityHolder: ForegroundActivityHolder) : ScopedFactoryBeanHandler {

	override val name: String
		get() = ACTIVITY_SCOPE

	override val isActive: Boolean
		get() = foregroundActivityHolder.currentActivity is FragmentActivity

	override fun <T :Any> getBean(name: String, factoryBean: ScopedFactoryBean<T>): T {
		val activity = foregroundActivityHolder.currentActivity as FragmentActivity
		@Suppress("UNCHECKED_CAST")
		val beanHolder: ActivityScopedBeanHolder<T> = ViewModelProviders.of(activity).get<ActivityScopedBeanHolder<*>>(name, ActivityScopedBeanHolder::class.java) as ActivityScopedBeanHolder<T>
		if (!beanHolder.containsBean()) {
			beanHolder.bean = factoryBean.produceBean()
		}

		val bean = beanHolder.bean
		if (bean is ActivityAware) {
			bean.setActivity(activity)
		}
		return bean
	}

	// ViewModels need to be public to work.
	class ActivityScopedBeanHolder<T :Any> : ViewModel() {
		lateinit var bean: T

		fun containsBean() = ::bean.isInitialized

		public override fun onCleared() {
			if (bean is ActivityAware) {
				(bean as ActivityAware).setActivity(null)
			}
		}
	}

	companion object {
		/**
		 * The name of the [Activity]-scope.
		 */
		const val ACTIVITY_SCOPE = "activity"
	}
}
