package rocks.frieler.android.beans

import android.content.Context
import android.content.res.AssetManager
import rocks.frieler.android.beans.BeanConfigurationsAssetScanner.Companion.BEAN_CONFIGURATIONS_ASSET_PATH
import rocks.frieler.kotlin.reflect.isAssignableFrom
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.jvm.jvmName

/**
 * Scans the assets for files defining [BeanConfiguration]s.
 *
 *
 * The files must reside in the directory [BEAN_CONFIGURATIONS_ASSET_PATH] and contain the full-qualified name of an
 * implementation of [BeanConfiguration] per line, either a class or an object.
 */
internal class BeanConfigurationsAssetScanner(private val context: Context) {

	companion object {
		const val BEAN_CONFIGURATIONS_ASSET_PATH = "bean-configurations"
	    val ANDROID_CONTEXT_TYPE = Context::class.createType()
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
			Class.forName(className).kotlin
		} catch (e: ClassNotFoundException) {
			throw BeanInstantiationException("failed to instantiate BeanConfiguration $className.", e)
		}

		if (BeanConfiguration::class.isAssignableFrom(beanConfigurationClass)) {
			@Suppress("UNCHECKED_CAST")
			return instantiateBeanConfiguration(beanConfigurationClass as KClass<out BeanConfiguration>)
		} else {
			throw BeanInstantiationException("$className does not implement BeanConfiguration.")
		}
	}

	private fun instantiateBeanConfiguration(beanConfigurationClass: KClass<out BeanConfiguration>): BeanConfiguration {
		if (beanConfigurationClass.objectInstance != null) {
			return beanConfigurationClass.objectInstance!!
		}

		beanConfigurationClass.constructors.find {
			it.parameters.size == 1 && it.parameters[0].type.isSupertypeOf(ANDROID_CONTEXT_TYPE)
		}?.apply {
			try {
				return call(context)
			} catch (e: Exception) {
				throw BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.jvmName + ".", e)
			}
		}

		beanConfigurationClass.constructors.find {
			it.parameters.isEmpty()
		}?.apply {
			try {
				return call()
			} catch (e: Exception) {
				throw BeanInstantiationException("failed to instantiate BeanConfiguration " + beanConfigurationClass.jvmName + ".", e)
			}
		}

		throw BeanInstantiationException(beanConfigurationClass.jvmName + " does not provide a suitable constructor.")
	}
}
