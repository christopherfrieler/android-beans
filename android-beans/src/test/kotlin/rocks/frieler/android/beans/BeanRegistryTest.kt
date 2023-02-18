package rocks.frieler.android.beans

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import assertk.assertions.isSameAs
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rocks.frieler.android.beans.scopes.ScopedFactoryBean
import rocks.frieler.android.beans.scopes.ScopedFactoryBeanHandler
import rocks.frieler.android.beans.scopes.prototype.PrototypeScopedFactoryBean
import rocks.frieler.android.beans.scopes.singleton.SingletonScopedFactoryBean
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class BeanRegistryTest {
    private val beanRegistry = BeanRegistry()

    private val parent = mock<BeansProvider>().apply {
        whenever(lookUpOptionalBean(any(), any<KClass<*>>())).thenReturn(null)
        whenever(lookUpOptionalBean(any<KClass<*>>())).thenReturn(null)
        whenever(lookUpBeans(any<KClass<*>>())).thenReturn(emptyList())
    }
    private val beanRegistryWithParent = BeanRegistry(parent)

    @Test
    fun `lookUpOptionalBean() by name and type returns null without a bean with the desired name`() {
        val bean = beanRegistry.lookUpOptionalBean("bean", Any::class)

        assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns null when the bean with the desired name is of an incompatible type`() {
        val name = "bean"
        val registeredBean = Any()

        beanRegistry.registerBean(name, registeredBean)
        val bean = beanRegistry.lookUpOptionalBean(name, BeanRegistryTest::class)

		assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns registered bean with the desired name and type`() {
        val name = "bean"
        val registeredBean = Any()

        beanRegistry.registerBean(name, registeredBean)
        val bean = beanRegistry.lookUpOptionalBean(name, Any::class)

        assertThat(bean).isSameAs(registeredBean)
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns bean with the desired name and type from parent if no local one matches`() {
        val name = "bean"
        val inheritedBean = Any()
        whenever(parent.lookUpOptionalBean(name, Any::class)).thenReturn(inheritedBean)

        val bean = beanRegistryWithParent.lookUpOptionalBean(name, Any::class)

        assertThat(bean).isSameAs(inheritedBean)
    }

    @Test
    fun `lookUpOptionalBean() by type returns null without a matching bean`() {
        beanRegistry.registerBean("bean", Any())
        val bean = beanRegistry.lookUpOptionalBean(BeanRegistryTest::class)

		assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by type returns single matching bean`() {
        val registeredBean = Any()

        beanRegistry.registerBean("bean", registeredBean)
        val bean = beanRegistry.lookUpOptionalBean(Any::class)

		assertThat(bean).isSameAs(registeredBean)
    }

    @Test
    fun `lookUpOptionalBean() by type returns one of multiple matching beans`() {
		val registeredBean1 = Any()
		val registeredBean2 = Any()

		beanRegistry.registerBean("bean1", registeredBean1)
		beanRegistry.registerBean("bean2", registeredBean2)
        val bean = beanRegistry.lookUpOptionalBean(Any::class)

		assertThat(bean).isIn(registeredBean1, registeredBean2)
    }

    @Test
    fun `lookUpOptionalBean() by type returns bean with the desired type from parent if no local one matches`() {
        val inheritedBean = Any()
        whenever(parent.lookUpOptionalBean(Any::class)).thenReturn(inheritedBean)

        val bean = beanRegistryWithParent.lookUpOptionalBean(Any::class)

        assertThat(bean).isSameAs(inheritedBean)
    }

    @Test
    fun `lookUpBeans() by type returns all beans assignable to the desired type`() {
        beanRegistry.registerBean("object", Any())
        beanRegistry.registerBean("long", 42L)
        beanRegistry.registerBean("double", 3.14)
        val numbers = beanRegistry.lookUpBeans(Number::class)

		assertThat(numbers).containsOnly(42L, 3.14)
    }

    @Test
    fun `lookUpBeans() by type returns all registered and inherited beans`() {
        val inheritedBean = Any()
        whenever(parent.lookUpBeans(Any::class)).thenReturn(listOf(inheritedBean))
        val registeredBean = Any()
        beanRegistryWithParent.registerBean(registeredBean)

        val beans = beanRegistryWithParent.lookUpBeans(Any::class)

        assertThat(beans).containsOnly(registeredBean, inheritedBean)
    }

    @Test
    fun `registerBean() without explicit name generates a name from the bean-type`() {
        val bean = Any()

        val generatedName = beanRegistry.registerBean(bean)

        assertThat(beanRegistry.lookUpBean(generatedName, Any::class)).isSameAs(bean)
    }

    @Test
    fun `generated bean names avoid collisions`() {
        val bean1 = Any()
        val bean2 = Any()

        val generatedNameForBean1 = beanRegistry.registerBean(bean1)
        val generatedNameForBean2 = beanRegistry.registerBean(bean2)

        assertThat(generatedNameForBean2).isNotEqualTo(generatedNameForBean1)
        assertThat(beanRegistry.lookUpBean(generatedNameForBean1, Any::class)).isSameAs(bean1)
		assertThat(beanRegistry.lookUpBean(generatedNameForBean2, Any::class)).isSameAs(bean2)
    }

    /* tests for bean-scopes: */
    private val beanScope: ScopedFactoryBeanHandler = mock()

    @BeforeEach
    fun setupBeanScope() {
        whenever(beanScope.name).thenReturn("aScope")
        beanRegistry.addBeanScope(beanScope)
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns null when the ScopedFactoryBean with desired name produces the wrong type`() {
        val name = "bean"
        val factoryBean: ScopedFactoryBean<Any> = mock()
        whenever(factoryBean.beanType).thenReturn(Any::class)

        beanRegistry.registerBean(name, factoryBean)
        val bean: Any? = beanRegistry.lookUpOptionalBean(name, BeanRegistryTest::class)

        assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns null without a suitable scope for a matching ScopedFactoryBean`() {
        val name = "bean"
        val factoryBean: ScopedFactoryBean<BeanRegistryTest> = mock()
        whenever(factoryBean.beanType).thenReturn(BeanRegistryTest::class)
        whenever(factoryBean.scope).thenReturn("otherScope")

        beanRegistry.registerBean(name, factoryBean)
        val bean = beanRegistry.lookUpOptionalBean(name, factoryBean.beanType)

        assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns null when the suitable ScopedFactoryBean is not active`() {
        whenever(beanScope.isActive).thenReturn(false)
        val name = "bean"
        val scope = beanScope.name
        val factoryBean: ScopedFactoryBean<BeanRegistryTest> = mock()
        whenever(factoryBean.beanType).thenReturn(BeanRegistryTest::class)
        whenever(factoryBean.scope).thenReturn(scope)

        beanRegistry.registerBean(name, factoryBean)
        val bean = beanRegistry.lookUpOptionalBean(name, factoryBean.beanType)

        assertThat(bean).isNull()
    }

    @Test
    fun `lookUpOptionalBean() by name and type returns scoped bean from active scope with the desired name and type`() {
        whenever(beanScope.isActive).thenReturn(true)
        val name = "bean"
        val scope = beanScope.name
        val scopedBean = Any()
        val factoryBean: ScopedFactoryBean<Any> = mock()
        whenever(factoryBean.beanType).thenReturn(Any::class)
        whenever(factoryBean.scope).thenReturn(scope)
        whenever(factoryBean.produceBean(beanRegistry)).thenReturn(scopedBean)
		whenever(beanScope.getBean<Any>(eq(name), any(), eq(beanRegistry)))
				.thenAnswer { invocation -> (invocation.getArgument(1) as ScopedFactoryBean<*>).produceBean(beanRegistry) }

        beanRegistry.registerBean(name, factoryBean)
        val bean = beanRegistry.lookUpOptionalBean(name, factoryBean.beanType)

        assertThat(bean).isSameAs(scopedBean)
    }

    @Test
    fun `lookUpOptionalBean() by type returns matching bean from ScopedFactoryBean`() {
        whenever(beanScope.isActive).thenReturn(true)
        val name = "bean"
        val scope = beanScope.name
        val factoryBean: ScopedFactoryBean<BeanRegistryTest> = mock()
        whenever(factoryBean.beanType).thenReturn(BeanRegistryTest::class)
        whenever(factoryBean.scope).thenReturn(scope)
        whenever(factoryBean.produceBean(beanRegistry)).thenReturn(this)
		whenever(beanScope.getBean<Any>(eq(name), any(), eq(beanRegistry)))
				.thenAnswer { invocation -> (invocation.getArgument(1) as ScopedFactoryBean<*>).produceBean(beanRegistry) }

        beanRegistry.registerBean(name, factoryBean)
        val bean = beanRegistry.lookUpOptionalBean(factoryBean.beanType)

        assertThat(bean).isSameAs(this)
    }

    @Test
    fun `lookUpBeans() by type returns all beans assignable to the desired type including scoped beans`() {
        whenever(beanScope.isActive).thenReturn(true)
        beanRegistry.registerBean("object", Any())
        beanRegistry.registerBean("long", 42L)
        val name = "double"
        val scope = beanScope.name
        val factoryBean: ScopedFactoryBean<Double> = mock()
        whenever(factoryBean.beanType).thenReturn(Double::class)
        whenever(factoryBean.scope).thenReturn(scope)
        whenever(factoryBean.produceBean(beanRegistry)).thenReturn(3.14)
		whenever(beanScope.getBean<Any>(eq(name), any(), eq(beanRegistry)))
				.thenAnswer { invocation -> (invocation.getArgument(1) as ScopedFactoryBean<*>).produceBean(beanRegistry) }

        beanRegistry.registerBean(name, factoryBean)
        val numbers = beanRegistry.lookUpBeans(Number::class)

		assertThat(numbers).containsOnly(42L, 3.14)
    }

    @Test
    fun `generated bean name for ScopedFactoryBeans is derived from produced bean-type`() {
        val factoryBean: ScopedFactoryBean<BeanRegistryTest> = mock()
        whenever(factoryBean.beanType).thenReturn(BeanRegistryTest::class)

        val generatedName = beanRegistry.registerBean(factoryBean)

        assertThat(generatedName).isEqualTo(this::class.jvmName)
    }

    @Test
    fun `BeanRegistry supports lazy instantiation in singleton-scope by default`() {
        val name = "lazyInstantiatedBean"
        val singletonFactory = SingletonScopedFactoryBean(BeanRegistryTest::class) { this@BeanRegistryTest }

        beanRegistry.registerBean(name, singletonFactory)
        val beanInstance = beanRegistry.lookUpBean(name, BeanRegistryTest::class)

        assertThat(beanInstance).isSameAs(this)
    }

    @Test
    fun `BeanRegistry supports prototype-scope by default`() {
        val name = "prototypeBean"
        val prototype = PrototypeScopedFactoryBean(BeanRegistryTest::class) { this@BeanRegistryTest }

        beanRegistry.registerBean(name, prototype)
        val beanInstance = beanRegistry.lookUpBean(name, BeanRegistryTest::class)

		assertThat(beanInstance).isSameAs(this)
    }

    /* tests for post-processing: */
    private val beanPostProcessor: BeanPostProcessor = mock()

    @Test
    fun `bean implementing BeanPostProcessor gets detected and receives new beans to post-process`() {
        val originalBean = Any()
        val replacementBean = Any()
        whenever(beanPostProcessor.postProcessBean("bean", originalBean)).thenReturn(replacementBean)

        beanRegistry.registerBean(beanPostProcessor)
        beanRegistry.registerBean("bean", originalBean)

		verify(beanPostProcessor).postProcessBean("bean", originalBean)
        assertThat(beanRegistry.lookUpBean("bean", Any::class)).isSameAs(replacementBean)
    }

    @Test
    fun `bean implementing BeanPostProcessor gets detected and receives existing beans to post-process`() {
        val originalBean = Any()
        val replacementBean = Any()
        whenever(beanPostProcessor.postProcessBean("bean", originalBean)).thenReturn(replacementBean)

        beanRegistry.registerBean("bean", originalBean)
        beanRegistry.registerBean(beanPostProcessor)

		verify(beanPostProcessor).postProcessBean("bean", originalBean)
		assertThat(beanRegistry.lookUpBean("bean", Any::class)).isSameAs(replacementBean)
    }

    @Test
    fun `bean implementing BeanPostProcessor receives scoped bean to post-process when it is produced`() {
        whenever(beanScope.isActive).thenReturn(true)
        val name = "bean"
        val scope = beanScope.name
        val originalBean = Any()
        val factoryBean: ScopedFactoryBean<Any> = mock()
        whenever(factoryBean.beanType).thenReturn(Any::class)
        whenever(factoryBean.scope).thenReturn(scope)
        whenever(factoryBean.produceBean(beanRegistry)).thenReturn(originalBean)
		whenever(beanScope.getBean<Any>(eq(name), any(), eq(beanRegistry)))
				.thenAnswer { invocation -> (invocation.getArgument(1) as ScopedFactoryBean<*>).produceBean(beanRegistry) }
        whenever(beanPostProcessor.postProcessBean(name, factoryBean)).thenReturn(factoryBean)
        val replacementBean = Any()
        whenever(beanPostProcessor.postProcessBean(name, originalBean)).thenReturn(replacementBean)

        beanRegistry.registerBean(name, factoryBean)
        beanRegistry.registerBean(beanPostProcessor)
		val finalBean = beanRegistry.lookUpBean(name, Any::class)

        verify(beanPostProcessor).postProcessBean(name, originalBean)
		assertThat(finalBean).isSameAs(replacementBean)
    }
}
