package rocks.frieler.android.beans

import android.content.Context
import android.content.res.AssetManager
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class BeanConfigurationsAssetScannerTest {
	private val context: Context = mock()
	private val beanConfigurationsAssetScanner = BeanConfigurationsAssetScanner(context)

	private val assets: AssetManager = mock()

	@Test
	fun `scan() returns list of BeanConfigurations`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(arrayOf("beans1.txt", "beans2.txt"))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans1.txt"))
				.thenReturn(ByteArrayInputStream(("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\$ABeanConfiguration\n" +
						"rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\$ABeanConfigurationNeedingTheContext\n").toByteArray()))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans2.txt"))
				.thenReturn(ByteArrayInputStream("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\$AnotherBeanConfiguration\n".toByteArray()))

		val beanConfigurations = beanConfigurationsAssetScanner.scan(assets)

		assertThat(beanConfigurations).hasSize(3)
		assertThat(beanConfigurations[0]).isInstanceOf(ABeanConfiguration::class.java)
		assertThat(beanConfigurations[1]).isInstanceOf(ABeanConfigurationNeedingTheContext::class.java)
		assertThat(beanConfigurations[2]).isInstanceOf(AnotherBeanConfiguration::class.java)
	}

	@Test
	fun `scan() can use a BeanConfiguration defined as object`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(arrayOf("beans.txt"))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
				.thenReturn(ByteArrayInputStream(("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\$ABeanConfigurationObject\n").toByteArray()))

		val beanConfigurations = beanConfigurationsAssetScanner.scan(assets)

		assertThat(beanConfigurations).contains(ABeanConfigurationObject)
	}

	@Test(expected = BeanInstantiationException::class)
	fun `scan() throws BeanInstantiationException when listing the assets fails`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenThrow(IOException::class.java)

		beanConfigurationsAssetScanner.scan(assets)
	}

	@Test(expected = BeanInstantiationException::class)
	fun `scan() throws BeanInstantiationException when reading an asset fails`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(arrayOf("beans.txt"))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt")).thenThrow(IOException::class.java)

		beanConfigurationsAssetScanner.scan(assets)
	}

	@Test(expected = BeanInstantiationException::class)
	fun `scan() throws BeanInstantiationException when specified class is not found`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(arrayOf("beans.txt"))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
				.thenReturn(ByteArrayInputStream("something.ThatIsNotAClass\n".toByteArray()))

		beanConfigurationsAssetScanner.scan(assets)
	}

	@Test(expected = BeanInstantiationException::class)
	fun `scan() throws BeanInstantiationException when specified class is not a BeanConfiguration`() {
		whenever(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(arrayOf("beans.txt"))
		whenever(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
				.thenReturn(ByteArrayInputStream("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\n".toByteArray()))

		beanConfigurationsAssetScanner.scan(assets)
	}

	class ABeanConfiguration : BeanConfiguration() {
		override fun getBeanDefinitions(): List<BeanDefinition<*>> {
			return emptyList()
		}
	}

	internal class ABeanConfigurationNeedingTheContext(
		@Suppress("UNUSED_PARAMETER") context: Context
	) : BeanConfiguration() {
		override fun getBeanDefinitions(): List<BeanDefinition<*>> {
			return emptyList()
		}
	}

	class AnotherBeanConfiguration : BeanConfiguration() {
		override fun getBeanDefinitions(): List<BeanDefinition<*>> {
			return emptyList()
		}
	}

	object ABeanConfigurationObject : BeanConfiguration() {
		override fun getBeanDefinitions(): List<BeanDefinition<*>> {
			return emptyList()
		}
	}
}
