package io.terraformkt.terraform

import io.terraformkt.hcl.HCLEntity
import java.io.File

/** Representation of file with Terraform code */
public class TFFile(val name: String, private val entities: MutableList<HCLEntity.Named> = ArrayList()): Comparable<TFFile>  {
    private val nameWithExt = "$name.tf"

    fun writeToDirectory(directory: File): File {
        require(directory.exists().not() || directory.isDirectory) { "TFFile can be written only to directory" }

        directory.mkdirs()

        val file = File(directory, nameWithExt)
        if (!file.exists()) file.createNewFile()

        file.writeText(buildString {
            for (entity in entities.sorted()) {
                append(entity.render())
                append("\n\n")
            }
        })

        return file
    }

    fun add(entity: HCLEntity.Named) {
        entities.add(entity)
    }

    override fun compareTo(other: TFFile): Int {
        return name.compareTo(other.name)
    }
}

fun tf(name: String, configure: TFFile.() -> Unit) = TFFile(name).apply(configure)
