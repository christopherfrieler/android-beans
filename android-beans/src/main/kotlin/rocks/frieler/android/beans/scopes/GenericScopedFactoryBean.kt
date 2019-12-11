package rocks.frieler.android.beans.scopes

import java8.util.function.Supplier

open class GenericScopedFactoryBean<T>(
        override val scope: String,
        override val beanType: Class<T>,
        private val producer: Supplier<T>)
    : ScopedFactoryBean<T> {

    override fun produceBean(): T {
        return producer.get()
    }
}
