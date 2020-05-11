package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class NoSuchBeanExceptionTest {
	@Test
	fun `NoSuchBeanException holds name and type`() {
		val noSuchBeanException = NoSuchBeanException("name", NoSuchBeanExceptionTest::class)

		assertThat(noSuchBeanException.name).isEqualTo("name")
		assertThat(noSuchBeanException.type).isEqualTo(NoSuchBeanExceptionTest::class)
	}

	@Test
	fun `message contains name and type of bean`() {
		val noSuchBeanException = NoSuchBeanException("name", NoSuchBeanExceptionTest::class)

		assertThat(noSuchBeanException.message).isEqualTo("No bean of type 'rocks.frieler.android.beans.NoSuchBeanExceptionTest' named 'name' found.")
	}

	@Test
	fun `message contains only type of bean when no name is specified`() {
		val noSuchBeanException = NoSuchBeanException(NoSuchBeanExceptionTest::class)

		assertThat(noSuchBeanException.message).isEqualTo("No bean of type 'rocks.frieler.android.beans.NoSuchBeanExceptionTest' found.")
	}
}
