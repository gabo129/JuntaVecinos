package com.example.juntavecinos.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CamaraUriFactory {

    fun crearUri(context: Context): Uri {

        val cameraDir = File(context.cacheDir, "camera")

        if (!cameraDir.exists() && !cameraDir.mkdirs()) {
            throw IllegalStateException(
                "No se pudo crear el directorio de la cámara"
            )
        }

        val photoFile = File.createTempFile(
            "comprobante_",
            ".jpg",
            cameraDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }
}