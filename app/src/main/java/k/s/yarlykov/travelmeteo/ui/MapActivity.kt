package k.s.yarlykov.travelmeteo.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.CityForecast
import k.s.yarlykov.travelmeteo.data.sources.openweather.api.OpenWeatherProvider
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.HourlyWeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.hourly.getDescription
import kotlinx.android.synthetic.main.activity_google_map.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, OpenWeatherProvider.ForecastReceiver {
    private var googleMap: GoogleMap? = null
    lateinit var locationManager: LocationManager
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        setSupportActionBar(bottom_app_bar)

        requestLocationPermissions()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId ?: 0

        when (id) {
            R.id.menuMapModeNormal -> googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.menuMapModeTerrain -> googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            R.id.menuMapModeSatellite -> googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.menuMapMyLocation -> {
                if (googleMap?.isMyLocationEnabled ?: false) {
                    navigateToMyLocation()
                }
            }
            R.id.menuMapTraffic -> {
                googleMap?.setTrafficEnabled(!(googleMap?.isTrafficEnabled ?: false))
            }
        }

        return true
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        initMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERM_LOCATION -> {
                if (grantResults.size == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)
                ) {
                    isPermissionGranted = true
                }
            }
        }

        if (isPermissionGranted) {
            recreate()
        }
    }

    override fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap) {
    }

    override fun onForecastHourly(model: HourlyWeatherResponseModel) {

        if(model == null) {
            logIt("hourly model is null")
            return
        }

        model.list?.let {
            for(f in it) {
                logIt(f.getDescription())
            }
        }
    }

    private fun navigateToMyLocation() {
        @SuppressLint("MissingPermission")
        val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        loc?.let {
            val target = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F)
            googleMap?.animateCamera(cameraUpdate)
        }
    }

    private fun initMap() {
        googleMap?.let {
            it.uiSettings.isZoomControlsEnabled = true
            it.uiSettings.isMyLocationButtonEnabled = false
            it.setPadding(0, 0, 0, dpToPix(80, this))

            it.setOnMapClickListener {
                logIt("Map clicked [${it.latitude} / ${it.longitude}]")
//                OpenWeatherProvider.requestForecastCurrent(this, it.latitude.toInt(), it.longitude.toInt())
                OpenWeatherProvider.requestForecastHourly(this, it.latitude.toInt(), it.longitude.toInt())

            }

            it.setOnMapLongClickListener {
                OpenWeatherProvider.requestForecastHourly(this, it.latitude.toInt(), it.longitude.toInt())
            }

            @SuppressLint("MissingPermission")
            it.isMyLocationEnabled = isPermissionGranted
        }
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                    REQUEST_PERM_LOCATION
                )
            }
        } else {
            isPermissionGranted = true
        }
    }

    companion object {
        private val EXTRA_MAP = MapActivity::class.java.simpleName + "extra.MAP"
        private const val REQUEST_PERM_LOCATION = 101

        fun start(context: Context?, extraData: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_MAP, extraData)
            }
            context?.startActivity(intent)
        }

        fun logIt(message: String?) {
            val TAG = "MapActivity"
            message?.let {
                Log.d(TAG, it)
            }
        }

        fun dpToPix(dp: Int, context: Context): Int {
            val displayMetrics = context.resources.getDisplayMetrics()
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
        }
    }
}