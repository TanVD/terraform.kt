package io.terraformkt.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer

val Project.mySourceSets: SourceSetContainer
    get() =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer
