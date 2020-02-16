package rocks.frieler.android.beans.scopes.activity

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import java8.util.function.Supplier
import org.junit.Test
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.Companion.activityScoped

class ActivityScopedFactoryBeanTest {
	private val producer: Supplier<ActivityScopedFactoryBeanTest> = mock()
	private val factoryBean = activityScoped(ActivityScopedFactoryBeanTest::class.java, producer)

	@Test
	fun `scope is Activity-scope`() {
		assertThat(factoryBean.scope).isEqualTo(ActivityScopedFactoryBeanHandler.ACTIVITY_SCOPE)
	}

	@Test
	fun `beanType is configured type`() {
		assertThat(factoryBean.beanType).isEqualTo(ActivityScopedFactoryBeanTest::class.java)
	}

	@Test
	fun `produceBean() calls the configured producer`() {
		whenever(producer.get()).thenReturn(this)

		assertThat(factoryBean.produceBean()).isSameAs(this)
	}
}
