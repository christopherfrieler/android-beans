package rocks.frieler.android.beans.scopes.singleton

import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler
import java.util.*

/**
 * [ScopedFactoryBeanHandler] for the [singleton][name]-scope.
 *
 *
 * It produces a new bean-instance only for the first time and re-uses it forever.
 */
class SingletonScopedFactoryBeanHandler : ScopedFactoryBeanHandler {
    private val beans: MutableMap<String, Any> = HashMap()

    override val name: String
        get() = SINGLETON_SCOPE

    override val isActive: Boolean
        get() = true

    override fun <T :Any> getBean(name: String, factoryBean: ScopedFactoryBean<T>): T {
        @Suppress("UNCHECKED_CAST") var bean = beans[name] as T?
        if (bean == null) {
            bean = factoryBean.produceBean()
            beans[name] = bean
        }
        return bean
    }

    companion object {
        const val SINGLETON_SCOPE = "singleton"
    }
}
