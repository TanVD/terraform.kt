package io.terraformkt.hcl

import io.terraformkt.utils.*


/** Field of HCL entity */
sealed class HCLField<T : Any>(override val hcl_name: String, private val entity: HCLEntity, var value: T?) : HCLNamed,
    HCLRender {
    override val renderable: Boolean
        get() = value != null

    override val hcl_ref: String by lazy { link(entity.myOwner?.hcl_ref, hcl_name) }
}

/** Field with entity owned by HCL entity */
class HCLEntityField<T : HCLEntity>(name: String, owner: HCLEntity, value: T?) : HCLField<T>(name, owner, value) {
    override fun render(): String {
        return """
            |$hcl_name = {
            |${value!!.render().withIndent()}
            |}
            """.trimMargin()
    }
}

/** Field with text owned by HCL entity */
class HCLTextField(name: String, owner: HCLEntity, value: String?) : HCLField<String>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${toText(value!!)}"
    }
}

/** Field with text list owned by HCL entity */
class HCLTextListField(name: String, owner: HCLEntity, value: Array<String>?) : HCLField<Array<String>>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${
            value!!.sortedArray().joinToString(prefix = "[", postfix = "]") {
                toText(it)
            }
        }"
    }
}

/** Field with bool owned by HCL entity */
class HCLBoolField(name: String, owner: HCLEntity, value: Boolean?) : HCLField<Boolean>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = $value"
    }
}

/** Field with bool list owned by HCL entity */
class HCLBoolListField(name: String, owner: HCLEntity, value: Array<Boolean>?) : HCLField<Array<Boolean>>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${value!!.sortedArray().joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}

/** Field with int owned by HCL entity */
class HCLIntField(name: String, owner: HCLEntity, value: Int?) : HCLField<Int>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = $value"
    }
}

/** Field with int list owned by HCL entity */
class HCLIntListField(name: String, owner: HCLEntity, value: Array<Int>?) : HCLField<Array<Int>>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${value!!.sortedArray().joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}

/** Field with any list owned by HCL entity */
class HCLAnyListField(name: String, owner: HCLEntity, value: Array<Any>?) : HCLField<Array<Any>>(name, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${
            value!!.joinToString(prefix = "[", postfix = "]") {
                anyToText(it)
            }
        }"
    }
}

