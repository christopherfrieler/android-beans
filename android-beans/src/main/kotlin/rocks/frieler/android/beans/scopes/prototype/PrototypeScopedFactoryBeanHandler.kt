package rocks.frieler.android.beans.scopes.prototype

import rocks.frieler.android.beans.BeansProvider
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler

/**
 * [ScopedFactoryBeanHandler] for the {@value #PROTOTYPE_SCOPE}-scope.
 *
 *
 * It produces a new bean-instance every time.
 */
class PrototypeScopedFactoryBeanHandler : ScopedFactoryBeanHandler {
    override val name: String
        get() = PROTOTYPE_SCOPE

    override val isActive: Boolean
        get() = true

    override fun <T :Any> getBean(name: String, factoryBean: ScopedFactoryBean<T>, dependencies: BeansProvider): T {
        return factoryBean.produceBean(dependencies)
    }

    companion object {
        const val PROTOTYPE_SCOPE = "prototype"
    }
}
