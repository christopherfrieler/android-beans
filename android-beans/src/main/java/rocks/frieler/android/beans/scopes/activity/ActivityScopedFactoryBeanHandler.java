package rocks.frieler.android.beans.scopes.activity;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import rocks.frieler.android.beans.scopes.ScopedFactoryBean;
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler;

/**
 * {@link ScopedFactoryBeanHandler} for the {@value #ACTIVITY_SCOPE}-scope.
 * <p>
 * It provides a bean-instance per {@link android.app.Activity}, always scoped to the
 * {@link ForegroundActivityHolder#getCurrentActivity() foregraound-activity}. Due to the limitation of the
 * {@link ViewModelProviders} this only works for subtypes of {@link FragmentActivity} such as the
 * {@code android.support.v7.app.AppCompatActivity}.
 * <p>
 * The {@link ActivityScopedFactoryBeanHandler} supports the {@link ActivityAware}-interface.
 */
public class ActivityScopedFactoryBeanHandler implements ScopedFactoryBeanHandler {
    /**
     * The name of the {@link Activity}-scope.
     */
    public static final String ACTIVITY_SCOPE = "activity";

    private final ForegroundActivityHolder foregroundActivityHolder;

    public ActivityScopedFactoryBeanHandler(ForegroundActivityHolder foregroundActivityHolder) {
        this.foregroundActivityHolder = foregroundActivityHolder;
    }

    @Override
    public String getName() {
        return ACTIVITY_SCOPE;
    }

    @Override
    public boolean isActive() {
        return (foregroundActivityHolder.getCurrentActivity() instanceof FragmentActivity);
    }

    @Nullable
    @Override
    public <T> T getBean(String name, ScopedFactoryBean<T> factoryBean) {
        final FragmentActivity activity = (FragmentActivity) foregroundActivityHolder.getCurrentActivity();

        //noinspection unchecked
        ActivityScopedBeanHolder<T> beanHolder = ViewModelProviders.of(activity).get(name, ActivityScopedBeanHolder.class);
        if (beanHolder.getBean() == null) {
            T bean = factoryBean.produceBean();
            beanHolder.setBean(bean);
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
