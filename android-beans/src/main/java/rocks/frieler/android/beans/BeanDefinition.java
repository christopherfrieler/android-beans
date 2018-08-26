package rocks.frieler.android.beans;

import java8.util.Optional;

/**
 * {@link BeanConfiguration} to define a single bean.
 */
public abstract class BeanDefinition extends BeanConfiguration {
    private final Optional<String> beanName;

    protected BeanDefinition() {
        this(null);
    }

    protected BeanDefinition(String beanName) {
        this.beanName = Optional.ofNullable(beanName);
    }

    public abstract Object bean();

    @Override
    public final void defineBeans(BeansCollector beansCollector) {
        if (beanName.isPresent()) {
            beansCollector.defineBean(beanName.get(), bean());
        } else {
            beansCollector.defineBean(bean());
        }
    }
}
