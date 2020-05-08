# Android Beans User-Manual


## Integrating Android Beans into an app

Make sure you have 'jcenter' added to your repositories in your build.gradle.kts file:
```kotlin
allprojects {
    repositories {
        jcenter()
    }
}
```

Add Android Beans to your dependencies:
```kotlin
dependencies {
    implementation("rocks.frieler.android:android-beans:0.3.0")
}
```

Declare the `BeanRegistryApplication` in your AndroidManifest.xml:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="my.app.package">
    <application android:name="rocks.frieler.android.beans.BeanRegistryApplication">
        ...
    </application>
</manifest>
```
The `BeanRegistryApplication` will bootstrap Android Beans on startup.

If you need your own Application-class for any further customizations, make sure it extends `BeanRegistryApplication`,
or, if extending `BeanRegistryApplication` is not suitable in your situation, you can use the `Beans.Initializer` to
bootstrap Android Beans.

Now you're ready to define your beans as described in the following section.


## Defining beans

To define beans write an object or a class that extends `BeanConfiguration`. The most convenient way is to extend its
subclass `DeclarativeBeanConfiguration`. In its `beans()`-method you can define your beans in a declarative fashion with
the various `bean()`-methods:
```kotlin
object MyBeanConfiguration : DeclarativeBeanConfiguration() {
    override fun beans() {
        bean { MyBean() } // with a generated name
        bean("myNamedBean") { MyBean() } // with an explicit name
    }
}
```
Or in Java:
```java
public class MyBeanConfiguration extends DeclarativeBeanConfiguration {
    @Override
    public void beans() {
        bean(MyBean.class, MyBean::new); // with a generated name
        bean("myNamedBean", MyBean.class, MyBean::new); // with an explicit name
    }
}
```
Of course you can also have multiple `BeanConfiguration`s to structure your code.

Now create a folder named `bean-configurations` in the assets-directory of your app and place a simple text-file with the
name of your choice  in it, which contains the full qualified name of your `BeanConfiguration`.
```text
my.app.package.MyBeanConfiguration
```
Android Beans will scan these files to find your`BeanConfiguration`s. You can declare one `BeanConfiguration` per line
and also have more than one such file. Hence, you can use the merge-behaviour of the assets-directory to define beans in
different modules and also Android libraries as long as the files have different names.


## Using beans

To use the previously defined beans you can look them up through the `Beans` facade-object, which offers three
possibilities.

**Lookup only by type:**
```kotlin
val myBean: MyBean = Beans.lookUpBean()
```
Or in Java:
```java
MyBean myBean = Beans.lookUpBean(MyBean.class);
```
This allows the consuming code to obtain an instance of the desired type without having to know how it is constructed
or even which implementation of an interface or abstract class it uses, since the returned bean can also be of a subtype
of the desired type. 

**Lookup by name and type:**
```kotlin
val myBean: MyBean = Beans.lookUpBean("myBean")
```
Or in Java:
```java
MyBean myBean = Beans.lookUpBean("myBean", MyBean.class);
```
This can be useful if there could be more than one bean of that type and you want a specific one.

**Lookup of all beans of a type**
```kotlin
val myBeans: List<MyBean> = Beans.lookUpBeans()
```
Or in Java:
```java
List<MyBean> myBeans = Beans.lookUpBeans(MyBean.class);
```
This way you can allow kind of a plugin-mechanism where other parts of the app can inject an additional implementation
of `MyBean` into the consuming code.

### Dependencies between beans 

You can also use beans from other `BeanConfiguration`s to define a bean, that depends on them. In order to handle
ordering between the `BeanConfiguration`s correctly, the dependency must be declared explicitly **before** the
invocation of `beans()`. `BeanConfiguration` provides some methods for that. Android Beans will ensure, that `beans()`
is called only after all dependencies are fulfilled. When you define the bean, you have access to a [BeansProvider] to
obtain the dependencies.
```kotlin
class MyBeanConfiguration : DeclarativeBeanConfiguration() {
    init {
        requireBean(type = MyDependency::class)
    }

    override fun beans() {
        bean("myBean") { MyBean(lookUpBean(MyDependency::class)) }
    }
}
```
Or in Java:
```java
public class MyBeanConfiguration extends DeclarativeBeanConfiguration {
    {
    	requireBean(MyDependency.class);
    }
    
    @Override
    public void beans() {
        bean("myBean", MyBean.class, (dependencies) -> new MyBean(dependencies.lookUpBean(MyDependency.class)));
    }
}
```
`BeanConfiguration` provides the following methods to require other beans:
- `requireBean(String?, KClass<T>)`: Requires a bean by name (optionally, if not `null`) and type.
- `requireOptionalBean(String?, KClass<T>)`: Declares an optional dependency on a bean by name (optionally, if not
`null`) and type. Android Beans will attempt to handle other `BeanConfiguration`s first to allow them to define such a
bean.
- `requireBeans(KClass<T>)`: Requires the beans of that type. Android Beans will attempt to handle other
`BeanConfiguration`s first to allow them to define such beans.
For convenient usage from Java there are also overloads with Java-`Class`es as arguments.

## Bean scopes

### Singleton scope

By default all beans are singletons, i.e. there exists one instance for the entire app.

Singleton beans can be created lazily. Therefore a bean must be defined through a `SingletonScopedFactoryBean` (which
comes with handy convenience methods for Kotlin and Java) instead of registering the actual bean-instance:
```kotlin
import rocks.frieler.android.beans.scopes.singleton.lazyInstantiatedBean

class SingletonScopeBeanConfiguration : DeclarativeBeanConfiguration() {
    override fun beans() {
        lazyInstantiatedBean { MyBean() }
    }
}
```
Or in Java:
```java
import static rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean.lazyInstantiated;

public class SingletonScopeBeanConfiguration extends DeclarativeBeanConfiguration {
    @Override
    public void beans() {
        bean(lazyInstantiated(MyBean.class, MyBean::new));
    }
}
```

### Prototype scope

In the prototype scope a new instance of the bean is created each time it is looked up. Prototype-scoped beans are
defined through a `PrototypeScopedFactoryBean` (which comes with handy convenience methods for Kotlin and Java)
instead of registering the actual bean-instance:
```kotlin
import rocks.frieler.android.beans.scopes.prototype.prototypeBean

class PrototypeScopeBeanConfiguration : DeclarativeBeanConfiguration() {
    override fun beans() {
        prototypeBean { MyBean() }
    }
}
```
Or in Java:
```java
import static rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean.prototype;

public class PrototypeScopeBeanConfiguration extends DeclarativeBeanConfiguration {
    @Override
    public void beans() {
        bean(prototype(MyBean.class, MyBean::new));
    }
}
```

### Activity scope

Beans can also be Activity-scoped. This means a new instance will be constructed for each `Activity`. (Note: Currently
the `Activity` must be a `ComponentActivity`.)
The bean will survive configuration changes of the `Activity` such as device-rotation, just like Android's `ViewModel`s.
(The implementation of Activity-scoped beans is actually based on them.) However, an Activity-scoped bean does not have
to extend `ViewModel`.

The consumer will always see the instance bound to the `Activity` which is currently in the foreground.

Activity-scoped beans are defined through an `ActivityScopedFactoryBean` (which comes with handy convenience methods
for Kotlin and Java) instead of registering the actual bean-instance:
```kotlin
import rocks.frieler.android.beans.scopes.activity.activityScopedBean

class ActivityScopeBeanConfiguration : DeclarativeBeanConfiguration() {
    override fun beans() {
        activityScopedBean { MyBean() }
    }
}
```
Or in Java:
```java
import static rocks.frieler.android.beans.scopes.activity.ActivityScopedFactoryBean.activityScoped;

public class ActivityScopeBeanConfiguration extends DeclarativeBeanConfiguration {
    @Override
    public void beans() {
        bean(activityScoped(MyBean.class, MyBean::new));
    }
}
```

Activity-scoped beans may also implement the `ActivityAware`-interface. This interface defines a single function
`setActivity(activity: Activity?)`, which will be invoked with the `Activity` instance whenever it changes, e.g. after a
configuration change of the `Activity`. At the end of the `Activity`'s lifecycle it will be invoked with `null`. Make
sure to clear all references to the `Activity`, otherwise the stale references could cause a memory-leak.


## Post-processing

### BeanPostProcessor

If you want to post-process beans after their creation, this is possible by implementing the
`BeanPostProcessor`-interface. The interface allows to manipulate or even replace beans.

A bean implementing `BeanPostProcessor`s is detected by the `BeanRegistry` automatically.

When a `BeanPostProcessor` is detected, it is first invoked for all existing beans. It will then be invoked for every
bean registered later and also for every scoped bean, when it is created by its factory.

Android Beans already provides one convenient implementation, the `BeansOfTypeConsumer`, which allows to apply an action
to all beans of a certain type. A common use-case is the registration of listeners:
```kotlin
class MyBeanConfiguration : DeclarativeBeanConfiguration() {
     private val myBean = requireBean(MyBean::class)
 
     override fun beans() {
         bean { BeansOfTypeConsumer(MyBeanListener::class, myBean.get()!!::registerListener) }
     }
 }
```
Or in Java:
```java
public class MyBeanConfiguration extends DeclarativeBeanConfiguration {
    private final BeanDependency<MyBean> myBean = requireBean(MyBean.class);

    @Override
    public void beans() {
        bean(BeansOfTypeConsumer.class, () -> 
            new BeansOfTypeConsumer(MyBeanListener.class, myBean.get()::registerListener));
    }
}
```

### BeanRegistryPostProcessor

Sometimes it may be necessary to post-process the entire `BeanRegistry`, which holds all the beans. This can be achieved
by implementing the `BeanRegistryPostProcessor`-interface. Once all beans are collected from the `BeanConfiguration`s,
the `postProcess(beanRegistry: BeanRegistry)`-function of all `BeanRegistryPostProcessor`-beans will be invoked.
