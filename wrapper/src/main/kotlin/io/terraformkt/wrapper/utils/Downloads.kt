package io.terraformkt.wrapper.utils

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

internal object Downloads {
    fun download(url: URL, toFile: File) {
        toFile.parentFile.mkdirs()
        FileOutputStream(toFile).channel.transferFrom(Channels.newChannel(url.openStream()), 0, java.lang.Long.MAX_VALUE)
    }

    fun download(url: URL, toFile: File, archive: Archive) {
        toFile.parentFile.mkdirs()
        val archiveFile = File(toFile.absolutePath + "." + archive.extension)

        download(url, archiveFile)

        archive.unarchive(archiveFile, toFile)

        archiveFile.delete()
    }
}
