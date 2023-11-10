package es.instituto.practicapermisos


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity() {


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCamera: ActivityResultLauncher<Void?>
    private val CAMERA_REQUEST_CODE = 1001

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adaptador: AdaptadorEntrada

    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var lastlocation: Location? = null
    private lateinit var texto: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración inicial
        this.configLocation()
        this.configRequests()

        // Inicialización del adaptador
        adaptador = AdaptadorEntrada(this)
        findViewById<ListView>(R.id.list_view).adapter = adaptador

        // Configuración del botón de peticion.
        findViewById<Button>(R.id.b_peticion).setOnClickListener {
            openCamera()
            startLocationUpdates()
        }
    }

    private fun openCamera() {
        // Solicita los permisos de la camara
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Abre la camara si los permisos fueron concedidos
            requestCamera.launch(null)
        } else {
            // Solicitar permisos si no se concedieron
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun startLocationUpdates() {

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission) {
            // Iniciar actualizaciones de ubicación si se concedieron los permisos
            locationRequest?.let { request ->
                locationCallback?.let { callback ->
                    fusedLocationClient.requestLocationUpdates(request, callback, null)
                }
            }
        } else {
            // Solicita permisos del gps si no fueron concedidos.
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun handleImage(bitmap: Bitmap) {
        // Guarda la ultima imagen capturada por la camara y su ultima locacización
        val newEntrada = Entrada(bitmap, lastlocation)
        adaptador.add(newEntrada) // Se agrega en el objeto de Adaptador entrada
        adaptador.notifyDataSetChanged() //Notifica la fecha una vez cambiada
    }

    private fun configLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false).setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                // Actualiza la ultima ubicacion.
                lastlocation = p0.lastLocation
            }
        }
    }

    private fun configRequests() {

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

        requestCamera = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) {
            // Envia la imagen guardada por la camara.
            it?.let { bitmap ->
                handleImage(bitmap)
            }
        }
    }
}

