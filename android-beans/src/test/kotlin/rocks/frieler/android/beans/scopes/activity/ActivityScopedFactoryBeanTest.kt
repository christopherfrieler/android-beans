package rocks.frieler.android.beans.scopes.activity

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isSameAs
import assertk.assertions.isSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.ScopedBeanDefinition
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

		assertThat(factoryBean.produceBean(dependencies)).isSameInstanceAs(this)
	}

	@Test
	fun `activityScoped() without dependencies provides a BeanDefinition for an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		val producerWithoutDependencies: () -> ActivityScopedFactoryBeanTest = mock()
		whenever(producerWithoutDependencies.invoke()).thenReturn(this)

		val activityScopedBeanDefinition = activityScoped(ActivityScopedFactoryBeanTest::class.java, producerWithoutDependencies)

		assertThat(activityScopedBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
		assertThat(activityScopedBeanDefinition.getType()).isEqualTo(ActivityScopedFactoryBean::class)
		assertActivityScopedFactoryBeanProducingThis(activityScopedBeanDefinition.produceBean(dependencies), dependencies)
	}

	@Test
	fun `activityScoped() provides a BeanDefinition for an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		whenever(producer(dependencies)).thenReturn(this)

		val activityScopedBeanDefinition = activityScoped(ActivityScopedFactoryBeanTest::class.java, producer)

		assertThat(activityScopedBeanDefinition).isInstanceOf(ScopedBeanDefinition::class)
		assertThat(activityScopedBeanDefinition.getType()).isEqualTo(ActivityScopedFactoryBean::class)
		assertActivityScopedFactoryBeanProducingThis(activityScopedBeanDefinition.produceBean(dependencies), dependencies)
	}

	@Test
	fun `DeclarativeBeanConfiguration_activityScopedBean() declares an ActivityScopedFactoryBean`() {
		val dependencies: BeansProvider = mock()
		val beanConfiguration : DeclarativeBeanConfiguration = mock()
		val beanName = "anActivityScopedBean"
		whenever(producer(dependencies)).thenReturn(this)

		beanConfiguration.activityScopedBean(beanName) { producer() }

		val beanDefinitionCaptor = argumentCaptor<BeanDefinition<ActivityScopedFactoryBean<ActivityScopedFactoryBeanTest>>>()
		verify(beanConfiguration).addBeanDefinition(beanDefinitionCaptor.capture())
		val beanDefinition = beanDefinitionCaptor.firstValue
		assertThat(beanDefinition).isInstanceOf(ScopedBeanDefinition::class)
		assertThat(beanDefinition.getName()).isEqualTo(beanName)
		assertThat(beanDefinition.getType()).isEqualTo(ActivityScopedFactoryBean::class)
		assertActivityScopedFactoryBeanProducingThis(beanDefinition.produceBean(dependencies), dependencies)
	}

	private fun assertActivityScopedFactoryBeanProducingThis(factoryBean: ActivityScopedFactoryBean<*>, dependencies: BeansProvider) {
		assertThat(factoryBean).isInstanceOf(ActivityScopedFactoryBean::class)
		assertThat(factoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class)
		assertThat(factoryBean.produceBean(dependencies)).isSameInstanceAs(this)
	}
}
