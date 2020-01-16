package k.s.yarlykov.travelmeteo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import k.s.yarlykov.travelmeteo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowMap.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnShowMap -> {
                MapActivity.start(this, "Some data")
            }
        }
    }
}
