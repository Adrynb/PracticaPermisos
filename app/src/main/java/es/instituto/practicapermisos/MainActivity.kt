package es.instituto.practicapermisos


import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity() {


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCamera: ActivityResultLauncher<Void?>


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adaptador: AdaptadorEntrada

    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var lastlocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.configLocation()
        this.configRequests()

        adaptador = AdaptadorEntrada(this)

        findViewById<ListView>(R.id.list_view).adapter = adaptador
        findViewById<Button>(R.id.b_peticion).setOnClickListener() {



        }


    }

    private fun configLocation() {
        //el servidor
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //la configuración de la actualización, se asocia a posteriori
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build();
        //que se ejecuta cuando se actualiza
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                //se obtiene la última coordenada
                lastlocation = p0.lastLocation
            }
        }
        //se asocia el servicio al tratamiento de la actualización
        fusedLocationClient.removeLocationUpdates(this.locationCallback as LocationCallback)

    }


    private fun configRequests() {
        //los dos launcher para permisos y fotografía
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(
                ), {
                    //no es necesario el tratamiento de esta actividad
                }
            )
        //se abre la cámara
        requestCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview(),
            //se trata la devolucion desde una función anónima
            {
            })
    }
}