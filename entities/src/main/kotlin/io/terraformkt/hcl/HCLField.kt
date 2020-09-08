package io.terraformkt.hcl

import io.terraformkt.utils.isLink
import io.terraformkt.utils.link
import io.terraformkt.utils.unlink
import io.terraformkt.utils.withIndent


/** Field of HCL entity */
sealed class HCLField<T : Any>(override val hcl_name: String, val inner: Boolean, private val entity: HCLEntity, var value: T?) : HCLNamed,
    HCLRender {
    override val renderable: Boolean
        get() = !inner && value != null

    override val hcl_ref: String by lazy { link(entity.myOwner?.hcl_ref, hcl_name) }
}

/** Field with entity owned by HCL entity */
class HCLEntityField<T : HCLEntity>(name: String, inner: Boolean, owner: HCLEntity, value: T?) : HCLField<T>(name, inner, owner, value) {
    override fun render(): String {
        return """
            |$hcl_name = {
            |${value!!.render().withIndent()}
            |}
            """.trimMargin()
    }
}

/** Field with text owned by HCL entity */
class HCLTextField(name: String, inner: Boolean, owner: HCLEntity, value: String?) : HCLField<String>(name, inner, owner, value) {
    companion object {
        fun toText(value: String) = if (isLink(value)) unlink(value) else "\"$value\""
    }

    override fun render(): String {
        return "$hcl_name = ${toText(value!!)}"
    }
}

/** Field with text list owned by HCL entity */
class HCLTextListField(name: String, inner: Boolean, owner: HCLEntity, value: Array<String>?) : HCLField<Array<String>>(name, inner, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${value!!.sortedArray().joinToString(prefix = "[", postfix = "]") {
            HCLTextField.toText(
                it
            )
        } }"
    }
}

/** Field with bool owned by HCL entity */
class HCLBoolField(name: String, inner: Boolean, owner: HCLEntity, value: Boolean?) : HCLField<Boolean>(name, inner, owner, value) {
    override fun render(): String {
        return "$hcl_name = $value"
    }
}

/** Field with bool list owned by HCL entity */
class HCLBoolListField(name: String, inner: Boolean, owner: HCLEntity, value: Array<Boolean>?) : HCLField<Array<Boolean>>(name, inner, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${value!!.sortedArray().joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}

/** Field with int owned by HCL entity */
class HCLIntField(name: String, inner: Boolean, owner: HCLEntity, value: Int?) : HCLField<Int>(name, inner, owner, value) {
    override fun render(): String {
        return "$hcl_name = $value"
    }
}

/** Field with int list owned by HCL entity */
class HCLIntListField(name: String, inner: Boolean, owner: HCLEntity, value: Array<Int>?) : HCLField<Array<Int>>(name, inner, owner, value) {
    override fun render(): String {
        return "$hcl_name = ${value!!.sortedArray().joinToString(prefix = "[", postfix = "]") { "$it" }}"
    }
}
