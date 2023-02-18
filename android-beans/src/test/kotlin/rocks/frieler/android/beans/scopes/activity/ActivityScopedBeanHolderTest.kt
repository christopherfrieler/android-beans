package rocks.frieler.android.beans.scopes.activity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.jupiter.api.Test
import rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBeanHandler.ActivityScopedBeanHolder

class ActivityScopedBeanHolderTest {
	private val beanHolder = ActivityScopedBeanHolder<Any>()

	@Test
	fun `onCleared() ignores Activity-unaware bean`() {
		val bean : Any = mock()

		beanHolder.bean = bean
		beanHolder.onCleared()

		verifyZeroInteractions(bean)
	}

	@Test
	fun `onCleared() clears ActivityAware bean`() {
		val bean : ActivityAware = mock()

		beanHolder.bean = bean
		beanHolder.onCleared()

		verify(bean).setActivity(null)
	}
}
