package rocks.frieler.android.beans

import android.app.Application
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.activity.ForegroundActivityHolder

/**
 * [Application] that sets up a [BeanRegistry], initialized by a [BeanConfigurationsBeansCollector].
 *
 *
 * In your application, use [Beans] to access the beans.
 *
 *
 * The [BeanRegistry] supports the [ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE].
 */
class BeanRegistryApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		Beans.Initializer()
				.addScope(activityScope())
				.collectBeans(scanAssetsForBeanConfigurations())
				.initialize()
	}

	private fun activityScope(): ActivityScopedFactoryBeanHandler {
		val foregroundActivityHolder = ForegroundActivityHolder()
		registerActivityLifecycleCallbacks(foregroundActivityHolder)
		return ActivityScopedFactoryBeanHandler(foregroundActivityHolder)
	}

	private fun scanAssetsForBeanConfigurations(): List<BeanConfiguration> {
		return BeanConfigurationsAssetScanner(this).scan(assets)
	}
}
