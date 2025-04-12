package rocks.frieler.android.beans

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.view.LayoutInflater
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AndroidInfrastructureBeansProviderTest {
	private val application = mock<Application>()
	private val androidInfrastructureBeansProvider = AndroidInfrastructureBeansProvider(application)

	@Test
	fun `lookUpOptionalBean by name and type provides the PackageManager`() {
		val packageManager = mock<PackageManager>()
		whenever(application.packageManager).thenReturn(packageManager)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean("PackageManager", PackageManager::class)

		assertThat(bean).isSameInstanceAs(packageManager)
	}

	@Test
	fun `lookUpOptionalBean by name and type provides the Resources`() {
		val resources = mock<Resources>()
		whenever(application.resources).thenReturn(resources)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean("Resources", Resources::class)

		assertThat(bean).isSameInstanceAs(resources)
	}

	@Test
	fun `lookUpOptionalBean by name and type provides the AssetManager`() {
		val assetManager = mock<AssetManager>()
		whenever(application.assets).thenReturn(assetManager)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean("AssetManager", AssetManager::class)

		assertThat(bean).isSameInstanceAs(assetManager)
	}

	@Test
	fun `lookUpOptionalBean by name and type provides the ContentResolver`() {
		val contentResolver = mock<ContentResolver>()
		whenever(application.contentResolver).thenReturn(contentResolver)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean("ContentResolver", ContentResolver::class)

		assertThat(bean).isSameInstanceAs(contentResolver)
	}

	@Test
	fun `lookUpOptionalBean by name and type provides system-service`() {
		whenever(application.getSystemServiceName(LayoutInflater::class.java)).thenReturn(Context.LAYOUT_INFLATER_SERVICE)
		val layoutInflater = mock<LayoutInflater>()
		whenever(application.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater::class)

		assertThat(bean).isSameInstanceAs(layoutInflater)
	}

	@Test
	fun `lookUpOptionalBean by type provides the PackageManager`() {
		val packageManager = mock<PackageManager>()
		whenever(application.packageManager).thenReturn(packageManager)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(PackageManager::class)

		assertThat(bean).isSameInstanceAs(packageManager)
	}

	@Test
	fun `lookUpOptionalBean by type provides the Resources`() {
		val resources = mock<Resources>()
		whenever(application.resources).thenReturn(resources)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(Resources::class)

		assertThat(bean).isSameInstanceAs(resources)
	}

	@Test
	fun `lookUpOptionalBean by type provides the AssetManager`() {
		val assetManager = mock<AssetManager>()
		whenever(application.assets).thenReturn(assetManager)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(AssetManager::class)

		assertThat(bean).isSameInstanceAs(assetManager)
	}

	@Test
	fun `lookUpOptionalBean by type provides the ContentResolver`() {
		val contentResolver = mock<ContentResolver>()
		whenever(application.contentResolver).thenReturn(contentResolver)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(ContentResolver::class)

		assertThat(bean).isSameInstanceAs(contentResolver)
	}

	@Test
	fun `lookUpOptionalBean by type provides system-service`() {
		whenever(application.getSystemServiceName(LayoutInflater::class.java)).thenReturn(Context.LAYOUT_INFLATER_SERVICE)
		val layoutInflater = mock<LayoutInflater>()
		whenever(application.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater)

		val bean = androidInfrastructureBeansProvider.lookUpOptionalBean(LayoutInflater::class)

		assertThat(bean).isSameInstanceAs(layoutInflater)
	}

	@Test
	fun `lookUpBeans by type provides the PackageManager by exact type`() {
		val packageManager = mock<PackageManager>()
		whenever(application.packageManager).thenReturn(packageManager)

		val beans = androidInfrastructureBeansProvider.lookUpBeans(PackageManager::class)

		assertThat(beans).containsOnly(packageManager)
	}

	@Test
	fun `lookUpBeans by type provides system-service by exact type`() {
		whenever(application.getSystemServiceName(LayoutInflater::class.java)).thenReturn(Context.LAYOUT_INFLATER_SERVICE)
		val layoutInflater = mock<LayoutInflater>()
		whenever(application.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater)

		val beans = androidInfrastructureBeansProvider.lookUpBeans(LayoutInflater::class)

		assertThat(beans).containsOnly(layoutInflater)
	}

	@Test
	fun `lookUpBeans by type provides no beans by super-type`() {
		whenever(application.getSystemServiceName(Any::class.java)).thenReturn(Context.LAYOUT_INFLATER_SERVICE)

		val beans = androidInfrastructureBeansProvider.lookUpBeans(Any::class)

		assertThat(beans).isEmpty()
	}
}
