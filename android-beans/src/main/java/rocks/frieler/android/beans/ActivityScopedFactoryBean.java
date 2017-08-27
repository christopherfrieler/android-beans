package rocks.frieler.android.beans;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

/**
 * Superclass for beans, that act as a factory for an {@link Activity}-scoped bean.
 * <p>
 * Due to the limitation of the {@link ViewModelProviders} this only works for subtypes of {@link FragmentActivity} such
 * as the {@code android.support.v7.app.AppCompatActivity}.
 * <p>
 * The {@link ActivityScopedFactoryBean} supports beans implementing {@link ActivityAware}.
 *
 * @param <T> the type of the bean this {@link ActivityScopedFactoryBean} produces
 *
 * @author Christopher Frieler
 */
public abstract class ActivityScopedFactoryBean<T> {
    /**
     * Returns the type of the bean this {@link ActivityScopedFactoryBean} produces.
     *
     * @return the type of the bean this {@link ActivityScopedFactoryBean} produces
     */
    protected abstract Class<T> getType();

    /**
     * Produces a new bean of type {@link T} corresponding to the given {@link Activity}.
     *
     * @return a new bean of type {@link T} corresponding to the given {@link Activity}
     */
    protected abstract T produceBean();

    T getBean(String name, FragmentActivity activity) {
        //noinspection unchecked
        ActivityScopedBeanHolder<T> beanHolder = ViewModelProviders.of(activity).get(name, ActivityScopedBeanHolder.class);
        if (beanHolder.getBean() == null) {
            beanHolder.setBean(produceBean());
        }
        if (beanHolder.getBean() instanceof ActivityAware) {
            ((ActivityAware) beanHolder.getBean()).setActivity(activity);
        }
        return beanHolder.getBean();
    }

    @SuppressWarnings("WeakerAccess") // ViewModels need to be public to work.
    public static class ActivityScopedBeanHolder<T> extends ViewModel {
        private T bean;

        public T getBean() {
            return bean;
        }

        public void setBean(T bean) {
            this.bean = bean;
        }

        @Override
        protected void onCleared() {
            if (bean instanceof ActivityAware) {
                ((ActivityAware) bean).setActivity(null);
            }
        }
    }
}
