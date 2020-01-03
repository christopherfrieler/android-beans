package rocks.frieler.android.beans

import java8.util.function.Consumer

/**
 * [BeanPostProcessor] to consume all beans of a certain type (including all its subtypes).
 *
 *
 * A common use-case is to collect all beans implementing a certain interface and inject them lazily into another bean.
 *
 * @param <Type> the type of beans to post-process
 */
class BeansOfTypeConsumer<Type>(private val type: Class<Type>, private val consumer: Consumer<Type>) : BeanPostProcessor {

    override fun <T :Any> postProcessBean(name: String, bean: T): T {
        if (type.isAssignableFrom(bean.javaClass)) {
            @Suppress("UNCHECKED_CAST")
            consumer.accept(bean as Type)
        }
        return bean
    }
}
