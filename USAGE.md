# Android Beans User-Manual


## Integrating Android Beans into an app

Make sure you have 'jcenter' added to your repositories in your build.gradle file:
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
```

Add Android Beans to your dependencies:
```groovy
dependencies {
    implementation 'rocks.frieler.android:android-beans:0.1.0-alpha1'
}
```

Declare the BeanRegistryApplication in your AndroidManifest.xml:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="my.app.package">
    <application android:name="rocks.frieler.android.beans.BeanRegistryApplication">
        ...
    </application>
</manifest>
```
The BeanRegistryApplication will bootstrap Android Beans on startup. If you need your own Application-class for any
further customizations, make sure it extends `BeanRegistryApplication`.

Now you're ready to define your beans as described in the following section.


## Defining beans

To define beans write a class that extends `BeanConfiguration`. In its `defineBeans()`-method you can define your beans
through the `BeansCollector`.
```java
public class MyBeanConfiguration extends BeanConfiguration {
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        beansCollector.defineBean(new MyBean()); // with a generated name
        beansCollector.defineBean("myNamedBean", new MyBean()); // with an explicit name
    }
}
```
Of course you can also have multiple `BeanConfiguration`s to structure your code.

Now create a folder named `bean-configurations` in the assets-directory of your app and place a simple text-file in it,
which contains the full qualified name of your `BeanConfiguration`.
```text
my.app.package.MyBeanConfiguration
```
Android Beans will scan these files to find your`BeanConfiguration`s. You can declare one `BeanConfiguration` per line
and also have more than one such file. Hence, you can use the merge-behaviour of the assets-directory to define beans in
different modules and also Android libraries as long as the files have different names.

### Defining a single bean

Sometimes you may want to define only a single bean. For these cases Android Beans provides a convenience class, the
`BeanDefinition`. It works like `BeanConfiguration`, but requires only to implement the actual construction of the bean
and optionally specify the name for the bean through the constructor.


## Using beans

To use the previously defined beans you can lookup them through the `Beans` facade-class, which offers three
possibilities.

**Lookup only by type:**
```java
MyBean myBean = Beans.lookUpBean(MyBean.class);
```
This allows the consuming code to obtain an instance of the desired type without having to know how it is constructed
or even which implementation of an interface or abstract class it uses, since the returned bean can also be of a subtype
of the desired type. 

**Lookup by name and type:**
```java
MyBean myBean = Beans.lookUpBean("myBean", MyBean.class);
```
This can be useful if there could be more than one bean of that type and you want a specific one.

**Lookup of all beans of a type**
```java
List<MyBean> myBeans = Beans.lookUpBeans(MyBean.class);
```
This way you can allow kind of a plugin-mechanism where other parts of the app can inject an additional implementation
of `MyBean` into the consuming code.

### Dependencies between beans 

You can also require beans from other `BeanConfiguration`s while defining a bean, that depends on them. In order to
handle ordering between the `BeanConfiguration`s correctly, the dependency must be declared explicitly **before** the
invocation of `defineBeans(BeansCollector)`. `BeanConfiguration` provides some methods for that. Android Beans will
ensure, that `defineBeans(BeansCollector)` is called only after all dependencies are fulfilled.
```java
public class MyBeanConfiguration extends BeanConfiguration {
    private final BeanDependency<MyDependency> dependency = requireBean(MyDependency.class);
    
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        beansCollector.defineBean("myBean", new MyBean(dependency.get()));
    }
}
```
`BeanConfiguration` provides the following methods to require other beans:
- `requireBean(Class<T>)`: Requires a bean by type.
- `requireBean(String, Class<T>)`: Requires a bean by name and type.
- `requireOptionalBean(Class<T>)`: Declares an optional dependency on a bean by type. The dependency will be resolved
lazily to allow other `BeanConfiguration`s to define the bean first.
- `requireBeans(Class<T>)`: Requires the beans of that type. Note: Since Android Beans cannot know these beans in
advance, this dependency is always seen as fulfilled. However, the resolution of this dependency will attempt to handle
as many `BeanConfiguration`s as possible first to allow them to define such beans.


## Bean scopes

### Singleton scope

By default all beans are singletons, i.e. there exists one instance for the entire app.

Singleton beans can be created lazily. Therefore a bean must be defined through a `SingletonScopedFactoryBean` (which
comes with the handy static convenience method `lazy(...)`) instead of registering the actual bean-instance:
```java
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazy;

public class ActivityScopeBeanConfiguration extends BeanConfiguration {
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        beansCollector.defineBean(lazy(MyBean.class, MyBean::new));
    }
}
```

### Prototype scope

In the prototype scope a new instance of the bean is created each time it is looked up. Prototype-scoped beans are
defined through a `PrototypeScopedFactoryBean` (which comes with the handy static convenience method `prototype(...)`)
instead of registering the actual bean-instance:
```java
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.prototype;

public class ActivityScopeBeanConfiguration extends BeanConfiguration {
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        beansCollector.defineBean(prototype(MyBean.class, MyBean::new));
    }
}
```

### Activity scope

Beans can also be Activity-scoped. This means a new instance will be constructed for each `Activity`. (Note: Currently
the `Activity` must be a `FragmentActivity`.)
The bean will survive configuration changes of the `Activity` such as device-rotation, just like Android's `ViewModel`s.
(The implementation of Activity-scoped beans is actually based on them.) However, an Activity-scoped bean does not have
to extend `ViewModel`.

The consumer will always see the instance bound to the `Activity` which is currently in the foreground.

Activity-scoped beans are defined through an `ActivityScopedFactoryBean` (which comes with the handy static convenience
method `activityScoped(...)`) instead of registering the actual bean-instance:
```java
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.activityScoped;

public class ActivityScopeBeanConfiguration extends BeanConfiguration {
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        beansCollector.defineBean(activityScoped(MyBean.class, MyBean::new));
    }
}
```

Activity-scoped beans may also implement the `ActivityAware`-interface. This interface defines a single method
`void setActivity(Activity)`, which will be invoked with the `Activity` instance whenever it changes, e.g. after a
configuration change of the `Activity`. At the and of the `Activity`'s lifecycle it will be invoked with `null`. Make
sure to clear all references to the `Activity`, otherwise the stale references would cause a memory-leak.


## Post-processing

### BeanPostProcessor

If you need to post-process beans after their creation, this is possible by implementing the
`BeanPostProcessor`-interface. The interface allows to manipulate or even replace beans.

`BeanPostProcessor`s must be registered at the `BeanRegistry`, usually through the `BeansCollector`.

When a `BeanPostProcessor` is registered, it is first invoked for all existing beans. It will then be invoked for every
bean registered later and also for every scoped bean, when it is created by its factory.

Android Beans already provides one convenient implementation, the `BeansOfTypeConsumer`, which allows to apply an action
to all beans of a certain type. A common use-case is the registration of listeners:
```java
public class MyBeanConfiguration extends BeanConfiguration {
    @Override
    public void defineBeans(BeansCollector beansCollector) {
        final MyBean myBean = new MyBean();
        beansCollector.defineBean(myBean);
        beansCollector.registerBeanPostProcessor(new BeansOfTypeConsumer(MyBeanListener.class, myBean::registerListener));
    }
}
```

### BeanRegistryPostProcessor

Sometimes it may be necessary to post-process the entire `BeanRegistry`, which holds all the beans. This can be achieved
by implementing the `BeanRegistryPostProcessor`-interface. Once all beans are collected from the `BeanConfiguration`s,
the `void postProcess(BeanRigistry)`-method of all `BeanRegistryPostProcessor`-beans will be invoked.
