package rocks.frieler.android.beans.scopes

import kotlin.reflect.KClass

open class GenericScopedFactoryBean<T : Any>(
        override val scope: String,
        override val beanType: KClass<T>,
        private val producer: () -> T)
    : ScopedFactoryBean<T> {

    override fun produceBean(): T {
        return producer()
    }
}
