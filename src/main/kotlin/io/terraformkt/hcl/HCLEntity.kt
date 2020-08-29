package io.terraformkt.hcl

import io.terraformkt.utils.withIndent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * Representation of HCL entity -- in general it is set of fields, like map
 *
 * HCLEntity not necessarily is [HCLNamed]
 */
open class HCLEntity(
    val myFields: LinkedHashSet<HCLField<*>> = LinkedHashSet(),
    val myInner: LinkedHashSet<HCLEntity> = LinkedHashSet(),
    open val myOwner: HCLNamed? = null
) : HCLRender {
    override val renderable: Boolean = true

    override fun render(): String = (myFields.filter { it.renderable } + myInner).joinToString(separator = "\n") {
        it.render()
    }

    abstract class Named: HCLEntity(), HCLNamed, Comparable<Named> {
        override fun compareTo(other: Named): Int = this.hcl_ref.compareTo(other.hcl_ref)
    }

    open class Inner(protected val tf_name: String) : HCLEntity() {
        override fun render(): String {
            return """
            |${tf_name} {
            |${super.render().withIndent()}
            |}
            """.trimMargin()
        }
    }

    inner class FieldProvider<T : Any, F : HCLField<T>>(val name: String?, val computed: Boolean, private val default: T?,
                                                        val getField: (name: String, renderable: Boolean, entity: HCLEntity, value: T?) -> F) {
        operator fun provideDelegate(entity: HCLEntity, property: KProperty<*>): FieldDelegate<T, F> {
            val field = getField(name ?: property.name, computed, entity, default)
            entity.myFields.add(field)
            return FieldDelegate(field)
        }
    }

    class FieldDelegate<T : Any, F : HCLField<T>>(private val field: F) : ReadWriteProperty<HCLEntity, T> {
        val hcl_ref: String by lazy { field.hcl_ref }
        val isSet: Boolean
            get() = this.field.value != null

        override fun getValue(thisRef: HCLEntity, property: KProperty<*>): T = field.value!!

        override fun setValue(thisRef: HCLEntity, property: KProperty<*>, value: T) {
            field.value = value
        }
    }

    fun <T : HCLEntity> entity(name: String? = null, computed: Boolean = false, default: T? = null): FieldProvider<T, HCLEntityField<T>> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLEntityField(field, renderable, entity, value)
        }
    }

    fun <T : Inner> inner(entity: T) {
        myInner.add(entity)
    }

    fun int(name: String? = null, computed: Boolean = false, default: Int? = null): FieldProvider<Int, HCLIntField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLIntField(field, renderable, entity, value)
        }
    }

    fun intArray(name: String? = null, computed: Boolean = false, default: Array<Int>? = null): FieldProvider<Array<Int>, HCLIntArrayField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLIntArrayField(field, renderable, entity, value)
        }
    }

    fun bool(name: String? = null, computed: Boolean = false, default: Boolean? = null): FieldProvider<Boolean, HCLBoolField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLBoolField(field, renderable, entity, value)
        }
    }

    fun boolArray(name: String? = null, computed: Boolean = false, default: Array<Boolean>? = null): FieldProvider<Array<Boolean>, HCLBoolArrayField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLBoolArrayField(field, renderable, entity, value)
        }
    }

    fun text(name: String? = null, computed: Boolean = false, default: String? = null): FieldProvider<String, HCLTextField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLTextField(field, renderable, entity, value)
        }
    }

    fun textArray(name: String? = null, computed: Boolean = false, default: Array<String>? = null): FieldProvider<Array<String>, HCLTextArrayField> {
        return FieldProvider(name, computed, default) { field, renderable, entity, value ->
            HCLTextArrayField(field, renderable, entity, value)
        }
    }
}

val <T : Any> KProperty0<T>.ref: String
    get() {
        this.isAccessible = true
        return (getDelegate() as HCLEntity.FieldDelegate<*, *>).hcl_ref
    }

val <T : Any> KProperty0<T>.isSet: Boolean
    get() {
        this.isAccessible = true
        return (getDelegate() as HCLEntity.FieldDelegate<*, *>).isSet
    }
