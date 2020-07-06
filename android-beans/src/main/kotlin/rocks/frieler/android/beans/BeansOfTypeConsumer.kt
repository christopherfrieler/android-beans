package rocks.frieler.android.beans

import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * [BeanPostProcessor] to consume all beans of a certain type (including all its subtypes).
 *
 * A common use-case is to collect all beans implementing a certain interface and inject them lazily
 * into another bean.
 *
 * @param <Type> the type of beans to post-process
 *
 * @author Christopher Frieler
 */
class BeansOfTypeConsumer<Type : Any>(private val type: KClass<Type>, private val consumer: (Type) -> Unit) : BeanPostProcessor {

    constructor(type: Class<Type>, consumer: Consumer<Type>) : this(type.kotlin, consumer::accept)

    override fun <T :Any> postProcessBean(name: String, bean: T): T {
        if (type.isInstance(bean)) {
            @Suppress("UNCHECKED_CAST")
            consumer(bean as Type)
        }
        return bean
    }
}
