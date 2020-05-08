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
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.Companion.activityScoped

class ActivityScopedFactoryBeanTest {
	private val producer: BeansProvider.() -> ActivityScopedFactoryBeanTest = mock()
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
		val dependencies: BeansProvider = mock()
		whenever(producer(dependencies)).thenReturn(this)

		assertThat(factoryBean.produceBean(dependencies)).isSameAs(this)
	}

	@Test
	fun `activityScoped() provides a Pair of java-type and definition without dependencies for an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		val producerWithoutDependencies: () -> ActivityScopedFactoryBeanTest = mock()
		whenever(producerWithoutDependencies.invoke()).thenReturn(this)

		val activityScopedBeanDefinition = activityScoped(ActivityScopedFactoryBeanTest::class.java, producerWithoutDependencies)

		assertThat(activityScopedBeanDefinition.first).isEqualTo(ActivityScopedFactoryBean::class.java)
		val definedFactoryBean = activityScopedBeanDefinition.second(dependencies)
		assertThat(definedFactoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(definedFactoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
	}

	@Test
	fun `activityScoped() provides a Pair of java-type and definition for an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		whenever(producer(dependencies)).thenReturn(this)

		val activityScopedBeanDefinition = activityScoped(ActivityScopedFactoryBeanTest::class.java, producer)

		assertThat(activityScopedBeanDefinition.first).isEqualTo(ActivityScopedFactoryBean::class.java)
		val definedFactoryBean = activityScopedBeanDefinition.second(dependencies)
		assertThat(definedFactoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(definedFactoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
	}

	@Test
	fun `DeclarativeBeanConfiguration_activityScopedBean() declares an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		val beanConfiguration : DeclarativeBeanConfiguration = mock()
		val beanName = "anActivityScopedBean"
		whenever(producer(dependencies)).thenReturn(this)

		beanConfiguration.activityScopedBean(beanName) { producer() }

		val factoryBeanDefinitionCaptor = argumentCaptor<BeansProvider.() -> ActivityScopedFactoryBean<*>>()
		verify(beanConfiguration).addBeanDefinition(eq(beanName), eq(ActivityScopedFactoryBean::class), factoryBeanDefinitionCaptor.capture())
		val definedFactoryBean = factoryBeanDefinitionCaptor.firstValue(dependencies)
		assertThat(definedFactoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(definedFactoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(definedFactoryBean.produceBean(dependencies)).isSameAs(this)
	}

}
