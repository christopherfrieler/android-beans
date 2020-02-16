package rocks.frieler.android.beans;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans the assets for files defining {@link BeanConfiguration}s.
 * <p>
 * The files must reside in the directory {@value BEAN_CONFIGURATIONS_ASSET_PATH} and contain the full-qualified name of
 * an implementation of {@link BeanConfiguration} per line.
 */
class BeanConfigurationsAssetScanner {
    static final String BEAN_CONFIGURATIONS_ASSET_PATH = "bean-configurations";

    private final Context context;

    BeanConfigurationsAssetScanner(Context context) {
        this.context = context;
    }

    /**
     * Scans the assets under {@value BEAN_CONFIGURATIONS_ASSET_PATH} for {@link BeanConfiguration}s.
     *
     * @param assets the {@link AssetManager assets} to scan
     * @return the {@link BeanConfiguration}s defined in the files under {@value BEAN_CONFIGURATIONS_ASSET_PATH}
     * @throws BeanInstantiationException when {@value BEAN_CONFIGURATIONS_ASSET_PATH} cannot be scanned, a
     * bean-configurations-file is corrupt or instantiating a {@link BeanConfiguration} fails
     */
    List<? extends BeanConfiguration> scan(AssetManager assets) {
        try {
            List<BeanConfiguration> beanConfigurations = new ArrayList<>();
            //noinspection ConstantConditions
            for (String file : assets.list(BEAN_CONFIGURATIONS_ASSET_PATH)) {
                String filePath = BEAN_CONFIGURATIONS_ASSET_PATH + "/" + file;
                beanConfigurations.addAll(processBeanConfigurationsFile(assets, filePath));
            }
            return beanConfigurations;
        } catch (IOException e) {
            throw new BeanInstantiationException("failed to scan " + BEAN_CONFIGURATIONS_ASSET_PATH + ".", e);
        }
    }

    private List<? extends BeanConfiguration> processBeanConfigurationsFile(AssetManager assets, String filePath) {
        try (BufferedReader beanConfigurationsReader = openBeanConfigurationsReader(filePath, assets)) {
            ArrayList<BeanConfiguration> beanConfigurations = new ArrayList<>();
            for (String line = beanConfigurationsReader.readLine(); line != null; line = beanConfigurationsReader.readLine()) {
                beanConfigurations.add(getBeanConfiguration(line));
            }
            return beanConfigurations;

        } catch (IOException e) {
            throw new BeanInstantiationException("failed to read " + filePath + ".", e);
        }
    }

    private BufferedReader openBeanConfigurationsReader(String file, AssetManager assets) throws IOException {
        return new BufferedReader(new InputStreamReader(assets.open(file)));
    }

    private BeanConfiguration getBeanConfiguration(String className) {
        try {
            Class<?> beanConfigurationClass = Class.forName(className);
            if (BeanConfiguration.class.isAssignableFrom(beanConfigurationClass)) {
                return instantiateBeanConfiguration(beanConfigurationClass);
            } else {
                throw new BeanInstantiationException(className + " does not implement BeanConfiguration.");
            }
        } catch (ClassNotFoundException e) {
            throw new BeanInstantiationException("failed to instantiate BeanConfiguration " + className + ".", e);
        }
    }

    private BeanConfiguration instantiateBeanConfiguration(Class<?> beanConfigurationClass) {
        try {
            return (BeanConfiguration) beanConfigurationClass.getConstructor(Context.class).newInstance(context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.getName() + ".", e);
        } catch (NoSuchMethodException e) {
            // no constructor with Context-parameter, try next one...
        }

        try {
            return (BeanConfiguration) beanConfigurationClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.getName() + ".", e);
        } catch (NoSuchMethodException e) {
            throw new BeanInstantiationException(beanConfigurationClass.getName() + " does not provide a suitable constructor.");
        }
    }
}
