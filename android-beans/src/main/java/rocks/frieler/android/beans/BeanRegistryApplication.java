package rocks.frieler.android.beans;

import android.app.Application;

import java.util.List;

import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler;
import rocks.frieler.android.beans.scopes.activity.ForegroundActivityHolder;
import rocks.frieler.android.facades.AssetManagerFacade;

/**
 * {@link Application} that sets up a {@link BeanRegistry}, initialized by a {@link BeanConfigurationsBeansCollector}.
 * <p>
 * In your application, use {@link Beans} to access the beans.
 * <p>
 * The {@link BeanRegistry} supports the {@link ActivityScopedFactoryBeanHandler#ACTIVITY_SCOPE}-scope.
 */
public class BeanRegistryApplication extends Application {
    private final BeanRegistry beanRegistry = new BeanRegistry();

    @Override
    public void onCreate() {
        super.onCreate();

        final ForegroundActivityHolder foregroundActivityHolder = new ForegroundActivityHolder();
        registerActivityLifecycleCallbacks(foregroundActivityHolder);
        beanRegistry.addBeanScope(new ActivityScopedFactoryBeanHandler(foregroundActivityHolder));

        initializeBeans();
    }

    private void initializeBeans() {
        AssetManagerFacade assets = new AssetManagerFacade(getAssets());
        List<? extends BeanConfiguration> beanConfigurations = new BeanConfigurationsAssetScanner(this).scan(assets);
        new BeanConfigurationsBeansCollector(beanRegistry).collectBeans(beanConfigurations);
        Beans.setBeans(beanRegistry);
    }
}
