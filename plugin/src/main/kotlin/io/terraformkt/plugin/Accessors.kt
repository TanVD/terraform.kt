package io.terraformkt.plugin

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val Project.mySourceSets: SourceSetContainer
    get() = (this as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer

val SourceSet.kotlin: SourceDirectorySet
    get() = (this as HasConvention)
        .convention
        .getPlugin(KotlinSourceSet::class.java)
        .kotlin


fun SourceSet.kotlin(action: SourceDirectorySet.() -> Unit) =
    kotlin.action()
