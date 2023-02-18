package rocks.frieler.android.beans.scopes.activity

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.ActivityScopedBeanHolder

class ActivityScopedBeanHolderTest {
	private val beanHolder = ActivityScopedBeanHolder<Any>()

	@Test
	fun `onCleared() ignores Activity-unaware bean`() {
		val bean : Any = mock()

		beanHolder.bean = bean
		beanHolder.onCleared()

		verifyNoInteractions(bean)
	}

	@Test
	fun `onCleared() clears ActivityAware bean`() {
		val bean : ActivityAware = mock()

		beanHolder.bean = bean
		beanHolder.onCleared()

		verify(bean).setActivity(null)
	}
}
