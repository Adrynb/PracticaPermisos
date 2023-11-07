package es.instituto.practicapermisos

import android.graphics.Bitmap
import android.location.Location

import java.sql.Timestamp
import java.time.LocalDateTime

    class Entrada {

        var imagen: Bitmap
        var location: Location?
        public constructor(
            imagen: Bitmap,
            location: Location?,

        ) {
            this.location = location
            this.imagen = imagen

        }
    }