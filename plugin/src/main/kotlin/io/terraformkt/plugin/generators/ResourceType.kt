package io.terraformkt.plugin.generators

import com.squareup.kotlinpoet.TypeSpec
import io.terraformkt.terraform.*

enum class ResourceType(val firstLetter: Char) {
    DATA('d'),
    RESOURCE('r'),
    PROVIDER('p'),

}

internal fun TypeSpec.Builder.addSuperClass(resourceType: ResourceType): TypeSpec.Builder {
    return when (resourceType) {
        ResourceType.RESOURCE -> {
            this.superclass(TFResource::class)
        }
        ResourceType.DATA -> {
            this.superclass(TFData::class)
        }
        ResourceType.PROVIDER -> {
            this.superclass(TFProvider::class)
        }
    }
}
