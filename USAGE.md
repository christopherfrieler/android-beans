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

To define beans write a class that implements `BeanConfiguration`. In its `defineBeans()`-method you can define your
beans through the `BeanConfigurationsBeansCollector`.
```java
public class MyBeanConfiguration implements BeanConfiguration {
    @Override
    public void defineBeans(BeanConfigurationsBeansCollector beansCollector) {
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

You can also lookup other beans while defining one, when they depend on each other. This is done through the
`BeanConfigurationsBeansCollector`, which offers the same three ways of lookup. Using the
`BeanConfigurationsBeansCollector` for the lookup will handle ordering between multiple `BeanConfigurations`.
```java
public class MyBeanConfiguration implements BeanConfiguration {
    @Override
    public void defineBeans(BeanConfigurationsBeansCollector beansCollector) {
        beansCollector.defineBean("myBean", new MyBean(beansCollector.lookUpBean(MyDependency.class)));
    }
}
```
