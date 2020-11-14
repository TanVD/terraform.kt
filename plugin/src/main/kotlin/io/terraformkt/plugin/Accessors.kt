package io.terraformkt.plugin

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val Project.mySourceSets: SourceSetContainer
    get() = (this as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer

val SourceSet.kotlin: SourceDirectorySet
    get() = (this as HasConvention)
        .convention
        .getPlugin(KotlinSourceSet::class.java)
        .kotlin

//Generated accessors to use in a plugin
internal inline fun <reified T : Any> Project.myExtByName(name: String): T = extensions.getByName<T>(name)

internal inline fun <reified T : Any> Project.myExt(name: String) = myExt[name] as T

internal val Project.myExt: ExtraPropertiesExtension
    get() = myExtByName("ext")
