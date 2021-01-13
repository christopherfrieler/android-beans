package rocks.frieler.android.beans

import android.app.Application
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.core.content.ContextCompat
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

/**
 * A [BeansProvider] that provides android infrastructure objects usually obtained through the
 * [android.content.Context] that exist on [Application]-level.
 *
 * Provided beans are
 * - the [PackageManager]
 * - the [Resources]
 * - the [AssetManager]
 * - the [ContentResolver]
 * - all [system-services][Application.getSystemService]
 */
class AndroidInfrastructureBeansProvider(
		private val application: Application,
) : BeansProvider {

	private val infrastructureTypes = arrayOf(
		PackageManager::class,
		Resources::class,
		AssetManager::class,
		ContentResolver::class,
	)

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> lookUpOptionalBean(name: String, type: KClass<T>): T? {
		return when {
			matchesNameAndTypeOf(name, type, PackageManager::class) -> application.packageManager as T
			matchesNameAndTypeOf(name, type, Resources::class) -> application.resources as T
			matchesNameAndTypeOf(name, type, AssetManager::class) -> application.assets as T
			matchesNameAndTypeOf(name, type, ContentResolver::class) -> application.contentResolver as T
			isSystemServiceWithName(name, type) -> ContextCompat.getSystemService(application, type.java)
			else -> null
		}
	}

	private fun matchesNameAndTypeOf(name: String, type: KClass<*>, typeToCheck: KClass<*>): Boolean {
		return (name == typeToCheck.simpleName && type.isSuperclassOf(typeToCheck))
	}

	private fun isSystemServiceWithName(name: String, type: KClass<*>): Boolean {
		return (ContextCompat.getSystemServiceName(application, type.java) == name)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> lookUpOptionalBean(type: KClass<T>): T? {
		return when {
			type.isSubclassOf(PackageManager::class) -> application.packageManager as T
			type.isSubclassOf(Resources::class) -> application.resources as T
			type.isSubclassOf(AssetManager::class) -> application.assets as T
			type.isSubclassOf(ContentResolver::class) -> application.contentResolver as T
			isSystemService(type) -> ContextCompat.getSystemService(application, type.java)
			else -> null
		}
	}

	private fun isSystemService(type: KClass<*>): Boolean {
		return (ContextCompat.getSystemServiceName(application, type.java) != null)
	}

	override fun <T : Any> lookUpBeans(type: KClass<T>): List<T> {
		return if (type in infrastructureTypes || isSystemService(type)) {
			listOf(lookUpBean(type))
		} else {
			emptyList()
		}
	}
}
