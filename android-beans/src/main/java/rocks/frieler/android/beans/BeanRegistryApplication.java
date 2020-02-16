package rocks.frieler.android.beans;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler;
import rocks.frieler.android.beans.scopes.activity.ForegroundActivityHolder;

/**
 * {@link Application} that sets up a {@link BeanRegistry}, initialized by a {@link BeanConfigurationsBeansCollector}.
 * <p>
 * In your application, use {@link Beans} to access the beans.
 * <p>
 * The {@link BeanRegistry} supports the {@link ActivityScopedFactoryBeanHandler#ACTIVITY_SCOPE}-scope.
 */
public class BeanRegistryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        new Beans.Initializer()
                .addScope(activityScope())
                .collectBeans(scanAssetsForBeanConfigurations())
                .initialize();
    }

    @NonNull
    private ActivityScopedFactoryBeanHandler activityScope() {
        final ForegroundActivityHolder foregroundActivityHolder = new ForegroundActivityHolder();
        registerActivityLifecycleCallbacks(foregroundActivityHolder);

        return new ActivityScopedFactoryBeanHandler(foregroundActivityHolder);
    }

    private List<? extends BeanConfiguration> scanAssetsForBeanConfigurations() {
        return new BeanConfigurationsAssetScanner(this).scan(getAssets());
    }
}
