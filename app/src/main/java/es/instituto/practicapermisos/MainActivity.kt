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

        // resetPermissions()

        this.configLocation()
        this.configRequests()

        adaptador = AdaptadorEntrada(this)

        findViewById<ListView>(R.id.list_view).adapter = adaptador
        findViewById<Button>(R.id.b_peticion).setOnClickListener() {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            } else {

                openCamera()
            }

            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))

            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?

            if (imageBitmap != null) {
                val nuevaEntrada = Entrada(imagen = imageBitmap, location = lastlocation)
                adaptador.add(nuevaEntrada)
                adaptador.notifyDataSetChanged()

                Toast.makeText(this, "Imagen capturada y agregada a la lista", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun configLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false).setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {

                lastlocation = p0.lastLocation
            }
        }

    }

        private fun configRequests() {
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(
                ), {

                })

            requestCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview(),

                {

                })
        }

    private fun resetPermissions() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}




