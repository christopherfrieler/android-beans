package rocks.frieler.android.beans.scopes.prototype

import rocks.frieler.android.beans.DeclarativeBeanConfiguration
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import kotlin.reflect.KClass

/**
 * [ScopedFactoryBean] for beans of the [PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE]-scope.
 */
class PrototypeScopedFactoryBean<T : Any>(type: KClass<T>, producer: () -> T)
    : GenericScopedFactoryBean<T>(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE, type, producer) {

    companion object {
        /**
         * Provides type and definition for a [PrototypeScopedFactoryBean] to produce a bean of the
         * given type using the given producer.
         *
         * @param type the type of bean produced
         * @param producer the producer to create new beans
         * @param <T> the type of bean produced
         * @return a new [PrototypeScopedFactoryBean]
         */
        @JvmStatic
        fun <T : Any> prototype(type: Class<T>, producer: () -> T): Pair<Class<PrototypeScopedFactoryBean<*>>, () -> PrototypeScopedFactoryBean<T>> {
            return Pair(PrototypeScopedFactoryBean::class.java, { PrototypeScopedFactoryBean(type.kotlin, producer) })
        }
    }
}

inline fun <reified T : Any> DeclarativeBeanConfiguration.prototypeBean(name: String? = null, noinline definition: () -> T) {
    bean(name) {
        PrototypeScopedFactoryBean(T::class, definition)
    }
}
