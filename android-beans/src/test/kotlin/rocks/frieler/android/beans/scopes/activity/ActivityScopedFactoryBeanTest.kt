package rocks.frieler.android.beans.scopes.activity

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.Companion.activityScoped

class ActivityScopedFactoryBeanTest {
	private val producer: () -> ActivityScopedFactoryBeanTest = mock()
	private val factoryBean = ActivityScopedFactoryBean(ActivityScopedFactoryBeanTest::class, producer)

	@Test
	fun `scope is Activity-scope`() {
		assertThat(factoryBean.scope).isEqualTo(ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE)
	}

	@Test
	fun `beanType is configured type`() {
		assertThat(factoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
	}

	@Test
	fun `produceBean() calls the configured producer`() {
		whenever(producer()).thenReturn(this)

		assertThat(factoryBean.produceBean()).isSameAs(this)
	}

	@Test
	fun `activityScoped() provides a Pair of java-type and definition for an ActivityScopedFactoryBean`() {
		whenever(producer()).thenReturn(this)

		val activityScopedBeanDefinition = activityScoped(ActivityScopedFactoryBeanTest::class.java, producer)

		assertThat(activityScopedBeanDefinition.first).isEqualTo(ActivityScopedFactoryBean::class.java)
		val definedFactoryBean = activityScopedBeanDefinition.second()
		assertThat(definedFactoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(definedFactoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(definedFactoryBean.produceBean()).isSameAs(this)
	}

	@Test
	fun `DeclarativeBeanConfiguration_activityScopedBean() declares an ActivityScopedFactoryBean`() {
		val beanConfiguration : DeclarativeBeanConfiguration = mock()
		val beanName = "anActivityScopedBean"
		whenever(producer()).thenReturn(this)

		beanConfiguration.activityScopedBean(beanName) { producer() }

		val factoryBeanDefinitionCaptor = argumentCaptor<() -> ActivityScopedFactoryBean<*>>()
		verify(beanConfiguration).addBeanDefinition(eq(beanName), eq(ActivityScopedFactoryBean::class), factoryBeanDefinitionCaptor.capture())
		val definedFactoryBean = factoryBeanDefinitionCaptor.firstValue()
		assertThat(definedFactoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(definedFactoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(definedFactoryBean.produceBean()).isSameAs(this)
	}

}
