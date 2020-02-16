package rocks.frieler.android.beans.scopes

import java8.util.function.Function

/**
 * Decorator for [ScopedFactoryBean]s to enhance their behaviour.
 *
 * @param <T> the type of bean produced
 */
class ScopedFactoryBeanDecorator<T> private constructor(private val delegate: ScopedFactoryBean<T>) : ScopedFactoryBean<T> {
    companion object {
        fun <T> decorate(scopedFactoryBean: ScopedFactoryBean<T>): ScopedFactoryBeanDecorator<T> {
            return ScopedFactoryBeanDecorator(scopedFactoryBean)
        }
    }

    override val scope: String
        get() = delegate.scope

    override val beanType: Class<T>
        get() = delegate.beanType

    private var postProcessing: Function<T, T>? = null

    override fun produceBean(): T {
        var bean = delegate.produceBean()
        postProcessing?.let { bean = it.apply(bean) }
        return bean
    }

    fun withPostProcessing(postProcessing: Function<T, T>): ScopedFactoryBeanDecorator<T> {
        this.postProcessing = postProcessing
        return this
    }
}