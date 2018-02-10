package rocks.frieler.android.beans;

/**
 * Callback-interface that allows to post-process a {@link BeanRegistry} after all beans are collected.
 * <p>
 * Implementers of this interface that are registered in a {@link BeanRegistry} are applied to it automatically.
 *
 * @author Christopher Frieler
 */
public interface BeanRegistryPostProcessor {
    void postProcess(BeanRegistry beanRegistry);
}
