package rocks.frieler.android.beans

import android.app.Application
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.activity.ForegroundActivityHolder

/**
 * [Application] that initializes a [BeanRegistry] from [BeanConfiguration]s.
 *
 * In your application you can use [Beans] to access the beans.
 *
 * The [BeanRegistry] additionally supports the [ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE].
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
