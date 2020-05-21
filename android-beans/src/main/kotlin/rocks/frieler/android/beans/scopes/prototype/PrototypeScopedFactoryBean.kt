package rocks.frieler.android.beans.scopes.prototype

import rocks.frieler.android.beans.BeanDefinition
import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE]-scope.
 */
class PrototypeScopedFactoryBean<T : Any>(type: KClass<T>, producer: BeansProvider.() -> T)
    : GenericScopedFactoryBean<T>(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE, type, producer) {

    companion object {
        /**
         * Provides a [BeanDefinition] for a [PrototypeScopedFactoryBean] that produces a bean of
         * the given type using the given producer without dependencies.
         *
         * @param type the type of bean produced
         * @param producer the producer to create new beans
         * @param <T> the type of bean produced
         * @return a [BeanDefinition] for a [PrototypeScopedFactoryBean]
         */
        @JvmStatic
        fun <T : Any> prototype(type: Class<T>, producer: () -> T): BeanDefinition<PrototypeScopedFactoryBean<*>> {
            return prototype(type, { _:BeansProvider -> producer() } as BeansProvider.() -> T)
        }

        /**
         * Provides a [BeanDefinition] for a [PrototypeScopedFactoryBean] that produces a bean of
         * the given type using the given producer with dependencies.
         *
         * @param type the type of bean produced
         * @param producer the producer to create new beans
         * @param <T> the type of bean produced
         * @return a [BeanDefinition] for a [PrototypeScopedFactoryBean]
         */
        @JvmStatic
        fun <T : Any> prototype(type: Class<T>, producer: BeansProvider.() -> T): BeanDefinition<PrototypeScopedFactoryBean<*>> {
            return BeanDefinition(type = PrototypeScopedFactoryBean::class) { PrototypeScopedFactoryBean(type.kotlin, producer) }
        }
    }
}

inline fun <reified T : Any> DeclarativeBeanConfiguration.prototypeBean(name: String? = null, noinline definition: BeansProvider.() -> T) {
    bean(name) {
        PrototypeScopedFactoryBean(T::class, definition)
    }
}
