package rocks.frieler.android.beans.scopes

import rocks.frieler.android.beans.BeansProvider
import kotlin.reflect.KClass

open class GenericScopedFactoryBean<T : Any>(
        override val scope: String,
        override val beanType: KClass<T>,
        private val producer: BeansProvider.() -> T)
    : ScopedFactoryBean<T> {

    override fun produceBean(dependencies: BeansProvider): T {
        return producer(dependencies)
    }
}
