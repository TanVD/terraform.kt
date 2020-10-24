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

    abstract class Named : HCLEntity(), HCLNamed, Comparable<Named> {
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

    inner class FieldProvider<T : Any, F : HCLField<T>>(
        val name: String?, private val default: T?,
        val getField: (name: String, entity: HCLEntity, value: T?) -> F
    ) {
        operator fun provideDelegate(entity: HCLEntity, property: KProperty<*>): FieldDelegate<T, F> {
            val field = getField(name ?: property.name, entity, default)
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

    fun <T : HCLEntity> entity(name: String? = null, default: T? = null): FieldProvider<T, HCLEntityField<T>> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLEntityField(field, entity, value)
        }
    }

    fun <T : Inner> inner(entity: T) {
        myInner.add(entity)
    }

    fun int(name: String? = null, default: Int? = null): FieldProvider<Int, HCLIntField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLIntField(field, entity, value)
        }
    }

    fun intList(name: String? = null, default: Array<Int>? = null): FieldProvider<Array<Int>, HCLIntListField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLIntListField(field, entity, value)
        }
    }

    fun bool(name: String? = null, default: Boolean? = null): FieldProvider<Boolean, HCLBoolField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLBoolField(field, entity, value)
        }
    }

    fun boolList(name: String? = null, default: Array<Boolean>? = null): FieldProvider<Array<Boolean>, HCLBoolListField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLBoolListField(field, entity, value)
        }
    }

    fun text(name: String? = null, default: String? = null): FieldProvider<String, HCLTextField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLTextField(field, entity, value)
        }
    }

    fun textList(name: String? = null, default: Array<String>? = null): FieldProvider<Array<String>, HCLTextListField> {
        return FieldProvider(name, default) { field, entity, value ->
            HCLTextListField(field, entity, value)
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
