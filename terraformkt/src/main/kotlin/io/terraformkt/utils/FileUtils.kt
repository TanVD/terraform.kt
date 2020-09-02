package io.terraformkt.utils

import java.io.File
import java.io.IOException

/**
 * This method is a workaround for the gradle plugin's problem with using mkdirs.
 *
 * It behaves almost like {@link java.io.File#mkdirs()} except for the
 * resolving the path against System.getProperty("user.dir") before calling canonicalFile.
 *
 * */
fun File.myMkdirs(): Boolean {
    if (exists()) {
        return false
    }

    if (mkdir()) {
        return true
    }
    val resolvedFile = File(System.getProperty("user.dir")).resolve(this)

    val canonFile: File = try {
        resolvedFile.canonicalFile
    } catch (e: IOException) {
        return false
    }
    val parent = canonFile.parentFile
    return parent != null && (parent.mkdirs() || parent.exists()) &&
        canonFile.mkdir()
}
