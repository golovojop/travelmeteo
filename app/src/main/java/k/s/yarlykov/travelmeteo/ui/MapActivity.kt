package k.s.yarlykov.travelmeteo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import k.s.yarlykov.travelmeteo.R

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    companion object {
        private val EXTRA_MAP = MapActivity::class.java.simpleName + "exrta.MAP"

        fun start(context: Context?, extraData: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_MAP, extraData)
            }
            context?.startActivity(intent)
        }
    }

    lateinit var googleMap:GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap =  map!!
    }




}