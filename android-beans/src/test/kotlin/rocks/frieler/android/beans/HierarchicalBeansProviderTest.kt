package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class HierarchicalBeansProviderTest {
	private val inheritedBean = object {}
	private val parent = mock<BeansProvider>().apply {
		whenever(this.lookUpOptionalBean("inheritedBean", inheritedBean::class)).thenReturn(inheritedBean)
		whenever(this.lookUpOptionalBean(inheritedBean::class)).thenReturn(inheritedBean)
		whenever(this.lookUpBeans(inheritedBean::class)).thenReturn(listOf(inheritedBean))
		whenever(this.lookUpBeans(Any::class)).thenReturn(listOf(inheritedBean))
	}

	private val hierarchicalBeansProviderWithParent = TestHierarchicalBeansProvider(this, parent)
	private val hierarchicalBeansProviderWithoutParent = TestHierarchicalBeansProvider(this)

	@Test
	fun `lookUpOptionalBean by name and type returns locally found matching bean`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean("localBean", HierarchicalBeansProviderTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpOptionalBean by name and type returns matching bean from parent if no local bean matches`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean("inheritedBean", inheritedBean::class)

		assertThat(bean).isSameAs(inheritedBean)
	}

	@Test
	fun `lookUpOptionalBean by name and type returns null without matching bean locally or from parent`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean("notExistingBean", Any::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpOptionalBean by name and type returns null without matching bean locally nor a parent`() {
		val bean = hierarchicalBeansProviderWithoutParent.lookUpOptionalBean("notExistingBean", Any::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpOptionalBean by type returns locally found matching bean`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean(HierarchicalBeansProviderTest::class)

		assertThat(bean).isSameAs(this)
	}

	@Test
	fun `lookUpOptionalBean by type returns matching bean from parent if no local bean matches`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean(inheritedBean::class)

		assertThat(bean).isSameAs(inheritedBean)
	}

	@Test
	fun `lookUpOptionalBean by type returns null without matching bean locally or from parent`() {
		val bean = hierarchicalBeansProviderWithParent.lookUpOptionalBean(Number::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpOptionalBean by type returns null without matching bean locally nor a parent`() {
		val bean = hierarchicalBeansProviderWithoutParent.lookUpOptionalBean(Number::class)

		assertThat(bean).isNull()
	}

	@Test
	fun `lookUpBeans by type returns matching local and parent beans`() {
		val beans = hierarchicalBeansProviderWithParent.lookUpBeans(Any::class)

		assertThat(beans).containsOnly(this, inheritedBean)
	}

	@Test
	fun `lookUpBeans by type returns only matching beans from parent without local ones`() {
		val beans = hierarchicalBeansProviderWithParent.lookUpBeans(inheritedBean::class)

		assertThat(beans).containsOnly(inheritedBean)
	}

	@Test
	fun `lookUpBeans by type returns only local matching beans without matching ones from parent`() {
		val beans = hierarchicalBeansProviderWithParent.lookUpBeans(HierarchicalBeansProviderTest::class)

		assertThat(beans).containsOnly(this)
	}

	@Test
	fun `lookUpBeans by type returns only matching local beans without parent`() {
		val beans = hierarchicalBeansProviderWithoutParent.lookUpBeans(Any::class)

		assertThat(beans).containsOnly(this)
	}

	private class TestHierarchicalBeansProvider(val bean: Any, parent: BeansProvider? = null) : HierarchicalBeansProvider(parent) {
		override fun <T : Any> lookUpOptionalLocalBean(name: String, type: KClass<T>): T? {
			@Suppress("UNCHECKED_CAST")
			return if (name == "localBean" && type.isSuperclassOf(bean::class)) bean as T else null
		}

		override fun <T : Any> lookUpOptionalLocalBean(type: KClass<T>): T? {
			@Suppress("UNCHECKED_CAST")
			return if (type.isSuperclassOf(bean::class)) bean as T else null
		}

		override fun <T : Any> lookUpLocalBeans(type: KClass<T>): List<T> {
			@Suppress("UNCHECKED_CAST")
			return if (type.isSuperclassOf(bean::class)) listOf(bean as T) else emptyList()
		}
	}
}
