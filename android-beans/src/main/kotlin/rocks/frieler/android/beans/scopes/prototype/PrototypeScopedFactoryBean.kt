package rocks.frieler.android.beans.scopes.prototype

import java8.util.function.Supplier
import rocks.frieler.android.beans.scopes.GenericScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBean

/**
 * [ScopedFactoryBean] for beans of the [PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE]-scope.
 */
class PrototypeScopedFactoryBean<T>
private constructor(type: Class<T>, producer: Supplier<T>) : GenericScopedFactoryBean<T>(PrototypeScopedFactoryBeanHandler.PROTOTYPE_SCOPE, type, producer) {

    companion object {
        /**
         * Creates a new [PrototypeScopedFactoryBean] to produce a bean of the given type using the given producer.
         *
         * @param type the type of bean produced
         * @param producer the producer to create new beans
         * @param <T> the type of bean produced
         * @return a new [PrototypeScopedFactoryBean]
         */
        @JvmStatic
        fun <T> prototype(type: Class<T>, producer: Supplier<T>): PrototypeScopedFactoryBean<T> {
            return PrototypeScopedFactoryBean(type, producer)
        }
    }
}