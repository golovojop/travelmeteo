package k.s.yarlykov.travelmeteo.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import k.s.yarlykov.travelmeteo.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val EXTRA_MAP = MapActivity::class.java.simpleName + "extra.MAP"

        fun start(context: Context?, extraData: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_MAP, extraData)
            }
            context?.startActivity(intent)
        }
    }

    private var googleMap: GoogleMap? = null

    lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.let {
            it.uiSettings.isZoomControlsEnabled = true

        }
    }


}