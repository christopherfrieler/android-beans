package rocks.frieler.android.beans;

import android.app.Application;

import java.util.List;

import rocks.frieler.android.facades.AssetManagerFacade;

/**
 * {@link Application} that sets up a {@link BeanRegistry}, initialized by a {@link BeanConfigurationsBeansCollector}.
 * <p>
 * In your application, use {@link Beans} to access the beans.
 *
 * @author Christopher Frieler
 */
public class BeanRegistryApplication extends Application {
    private final ForegroundActivityHolder foregroundActivityHolder = new ForegroundActivityHolder();
    private final BeanRegistry beanRegistry = new BeanRegistry();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(foregroundActivityHolder);
        initializeBeans();
    }

    private void initializeBeans() {
        AssetManagerFacade assets = new AssetManagerFacade(getAssets());
        List<? extends BeanConfiguration> beanConfigurations = new BeanConfigurationsAssetScanner(this).scan(assets);
        new BeanConfigurationsBeansCollector(beanRegistry).collectBeans(beanConfigurations);
        Beans.setBeans(beanRegistry);
    }
}
