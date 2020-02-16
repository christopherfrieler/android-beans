package rocks.frieler.android.beans

import android.content.Context
import android.content.res.AssetManager
import rocks.frieler.android.beans.BeanConfigurationsAssetScanner.Companion.BEAN_CONFIGURATIONS_ASSET_PATH
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * Scans the assets for files defining [BeanConfiguration]s.
 *
 *
 * The files must reside in the directory [BEAN_CONFIGURATIONS_ASSET_PATH] and contain the full-qualified name of an
 * implementation of [BeanConfiguration] per line.
 */
internal class BeanConfigurationsAssetScanner(private val context: Context) {

	companion object {
		const val BEAN_CONFIGURATIONS_ASSET_PATH = "bean-configurations"
	}

	/**
	 * Scans the assets under [BEAN_CONFIGURATIONS_ASSET_PATH] for [BeanConfiguration]s.
	 *
	 * @param assets the [assets][AssetManager] to scan
	 * @return the [BeanConfiguration]s defined in the files under {@value BEAN_CONFIGURATIONS_ASSET_PATH}
	 * @throws BeanInstantiationException when [BEAN_CONFIGURATIONS_ASSET_PATH] cannot be scanned, a
	 * bean-configurations-file is corrupt or instantiating a [BeanConfiguration] fails
	 */
	fun scan(assets: AssetManager): List<BeanConfiguration> {
		try {
			val beanConfigurations: MutableList<BeanConfiguration> = ArrayList()
			for (file in assets.list(BEAN_CONFIGURATIONS_ASSET_PATH)!!) {
				val filePath = "$BEAN_CONFIGURATIONS_ASSET_PATH/$file"
				beanConfigurations.addAll(processBeanConfigurationsFile(assets, filePath))
			}
			return beanConfigurations
		} catch (e: IOException) {
			throw BeanInstantiationException("failed to scan $BEAN_CONFIGURATIONS_ASSET_PATH.", e)
		}
	}

	private fun processBeanConfigurationsFile(assets: AssetManager, filePath: String): List<BeanConfiguration> {
		try {
			openBeanConfigurationsReader(filePath, assets).use { beanConfigurationsReader ->
				val beanConfigurations = ArrayList<BeanConfiguration>()
				var line = beanConfigurationsReader.readLine()
				while (line != null) {
					beanConfigurations.add(getBeanConfiguration(line))
					line = beanConfigurationsReader.readLine()
				}
				return beanConfigurations
			}
		} catch (e: IOException) {
			throw BeanInstantiationException("failed to read $filePath.", e)
		}
	}

	private fun openBeanConfigurationsReader(file: String, assets: AssetManager): BufferedReader {
		return BufferedReader(InputStreamReader(assets.open(file)))
	}

	private fun getBeanConfiguration(className: String): BeanConfiguration {
		val beanConfigurationClass = try {
			Class.forName(className)
		} catch (e: ClassNotFoundException) {
			throw BeanInstantiationException("failed to instantiate BeanConfiguration $className.", e)
		}

		if (BeanConfiguration::class.java.isAssignableFrom(beanConfigurationClass)) {
			return instantiateBeanConfiguration(beanConfigurationClass)
		} else {
			throw BeanInstantiationException("$className does not implement BeanConfiguration.")
		}
	}

	private fun instantiateBeanConfiguration(beanConfigurationClass: Class<*>): BeanConfiguration {
		try {
			return beanConfigurationClass.getConstructor(Context::class.java).newInstance(context) as BeanConfiguration
		} catch (e: NoSuchMethodException) {
			// no constructor with Context-parameter, try next one...
		} catch (e: Exception) {
			throw BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.name + ".", e)
		}

		return try {
			beanConfigurationClass.getConstructor().newInstance() as BeanConfiguration
		} catch (e: NoSuchMethodException) {
			throw BeanInstantiationException(beanConfigurationClass.name + " does not provide a suitable constructor.")
		} catch (e: Exception) {
			throw BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.name + ".", e)
		}
	}
}
