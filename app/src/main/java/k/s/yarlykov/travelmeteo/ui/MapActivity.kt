package k.s.yarlykov.travelmeteo.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.CustomForecast
import k.s.yarlykov.travelmeteo.data.domain.CustomForecastModel
import k.s.yarlykov.travelmeteo.data.domain.celsius
import k.s.yarlykov.travelmeteo.data.sources.openweather.model.current.WeatherResponseModel
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.ForecastConsumer
import k.s.yarlykov.travelmeteo.data.sources.unifiedprovider.WeatherProvider
import k.s.yarlykov.travelmeteo.extensions.deleteAll
import k.s.yarlykov.travelmeteo.extensions.dpToPix
import k.s.yarlykov.travelmeteo.extensions.screenRatioHeight
import k.s.yarlykov.travelmeteo.extensions.initFromModel
import k.s.yarlykov.travelmeteo.presenters.IMapView
import kotlinx.android.synthetic.main.activity_google_map.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_forecast.*
import kotlin.random.Random

class MapActivity : AppCompatActivity(), OnMapReadyCallback, ForecastConsumer, IMapView {

    lateinit var locationManager: LocationManager
    lateinit var markers: MutableList<Marker>
    private var googleMap: GoogleMap? = null
    private var isPermissionGranted = false
    private var lastForecastDate: CustomForecastModel? = null
    private var isPortrait: Boolean = false

    //region Activity Life Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Определить ориентацию экрана
        isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        // Загрузить нужный макет
        setContentView(if (isPortrait) R.layout.activity_google_map else R.layout.activity_google_map_lan)
        // Добавить AppBar
        setSupportActionBar(bottom_app_bar)

        // Запросить разрешение на работу с гео
        requestLocationPermissions()

        // Список маркеров на карте
        markers = mutableListOf()

        // Инициализация виджетов с учетом savedInstanceState
        initViews(savedInstanceState)

        // Подгрузить карту асинхронно
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        setBottomSheetSizing()
    }

    override fun onDestroy() {
        super.onDestroy()
        markers.deleteAll()
    }

    // Сохранить последний прогноз
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(FORECAST_KEY, lastForecastDate)
    }
    //endregion

    //region Options Menu
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
    //endregion

    //region Application Permissions
    // Запрос разрешений на работу с геопозицией
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
    //endregion

    //region ForecastConsumer
    override fun onContextRequest(): Context {
        return applicationContext
    }

    override fun onForecastCurrent(model: WeatherResponseModel, icon: Bitmap) {
    }

    override fun onForecastHourly(model: CustomForecastModel) {
        if (!model.list.isEmpty()) {
            updateForecastData(model)
        }
    }

    // Обновить контент в BottomSheet новыми данными
    private fun updateForecastData(model: CustomForecastModel?) {
        model?.let { m ->
            // Сохранить последний прогноз
            lastForecastDate = m

            m.list.let {
                // Установить название места
                tvCity.text = m.city
                // Установить иконку
                ivBkn.setImageResource(it[0].icon)
                // Установить температуру
                tvTemperature.text = it[0].celsius(it[0].temp)
                // Обновить RecycleView
                hourly.initFromModel(it)
                rvHourly.adapter?.notifyDataSetChanged()
                // Сменить видимость виджетов
                setBottomSheetVisibility(hideContent = false)
                // Выдвинуть шторку с виджетом
                setBottomSheetState(STATE_EXPANDED)
                // Установить картинку фона под прогноз
                val r = random(seasonImages.size)
                ivNatureBg.setImageDrawable(seasonImages[r])
            }
        }
    }
    //endregion

    //region GoogleMap Methods
    private fun initMap() {
        googleMap?.let {
            it.uiSettings.isZoomControlsEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false
            it.setPadding(0, 0, 0, dpToPix(80.toFloat()))

            it.setOnMapClickListener {
                //                logIt("Map clicked [${it.latitude} / ${it.longitude}]")
                setBottomSheetState(STATE_COLLAPSED)
                markers.deleteAll()
            }

            // Долгое нажатие на карту - запросить прогноз
            it.setOnMapLongClickListener { latLng ->
                // Запрос почасового прогноза для данной точки
                WeatherProvider.requestForecastHourly(this, latLng)
                // Пометить точку маркером на карте
                // https://developers.google.com/maps/documentation/android-sdk/marker
                addMarker(latLng)
            }

            @SuppressLint("MissingPermission")
            it.isMyLocationEnabled = isPermissionGranted
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        initMap()
    }

    private fun addMarker(latLng: LatLng) {
        googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .alpha(0.7f)
                .flat(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
            ?.apply {
                markers.deleteAll()
                markers.add(this)
            }
    }

    // Спозиционировать карту на мои текущие координаты
    private fun navigateToMyLocation() {
        @SuppressLint("MissingPermission")
        val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        loc?.let {
            val target = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F)
            googleMap?.animateCamera(cameraUpdate)
        }
    }
    //endregion

    //region IMapView implementation. BottomSheet Management
    // Инициализация вьюшек
    override fun initViews(savedInstanceState: Bundle?) {

        // Очистить список с прогнозами
        hourly.clear()

        // Скрыть шторку BottomSheet, потому что карта ещё не загружена
        setBottomSheetVisibility(hideContent = true)

        // Загрузить картинки природы для фона
        resources.obtainTypedArray(R.array.summerNature).let { typedArray ->
            seasonImages.clear()
            for (i in 0 until typedArray.length()) {
                seasonImages.add(ContextCompat.getDrawable(this, typedArray.getResourceId(i, R.drawable.home_logo)))
            }
            typedArray.recycle()
        }

        // Извлечение данных из savedState
        savedInstanceState?.let {
            updateForecastData(it.getParcelable(FORECAST_KEY) as? CustomForecastModel)
        }

        // Инициализация RecycleView
        rvHourly.apply {
            // Размер RV не зависит от изменения размеров его элементов
            setHasFixedSize(true)
            // Горизонтальная прокрутка
            layoutManager = LinearLayoutManager(this@MapActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = HourlyRVAdapter(hourly, applicationContext)
            itemAnimator = DefaultItemAnimator()
        }
    }

    /**
     * Materials:
     * https://medium.com/material-design-in-action/implementing-bottomappbar-behavior-fbfbc3a30568
     */
    // Установить положение шторки: свернута или выдвинута на высоту контента
    override fun setBottomSheetState(state: Int) {
        BottomSheetBehavior.from(bottom_sheet).state = state
    }

    // Расчитываем высоту "шапки" BottomSheet'a которая будет на BottomAppBar'ом
    override fun setBottomSheetSizing() {

        val bottomAppBarHeight = getActionBarHeight()!!

        // 1. Расчет и установка высоты верхней видимой части BottomSheet
        // в свернутом состоянии: то что будет видно над BottomAppBar.
        // Назовем эту видимую часть sheetCap

        // 1.1. Расстояние между двумя CardView (верхняя рамка)
        val shadowHeight = (cvNested.layoutParams as FrameLayout.LayoutParams).topMargin
        // 1.2. Высота видимой части BottomSheet
        val sheetCapHeight = with(vSlider.layoutParams as FrameLayout.LayoutParams) {
            2 * topMargin + height
        }
        // 2.1 Высота BottomAppBar
        val bottomAppBarBorderHeight = (vAppBarBorder.layoutParams as CoordinatorLayout.LayoutParams).height

        // 2.2 Складываем вместе и получаем peekHeight для BottomSheet
        val calculatedHeight =
            bottomAppBarHeight +
                    shadowHeight +
                    sheetCapHeight +
                    bottomAppBarBorderHeight

        // 2.3
        BottomSheetBehavior.from(bottom_sheet).peekHeight = calculatedHeight

        // 3 Позиционирование LinearLayout с прогнозом погоды внутри BottomSheet
        // 3.1 Расчет отступа контента от верней рамки BottomSheet
        val contentTopMargin =
            shadowHeight +
                    sheetCapHeight +
                    bottomAppBarBorderHeight
        // 3.2 Установка вычисленного значения через LayoutParams
        llForecast.layoutParams = (llForecast.layoutParams as FrameLayout.LayoutParams).apply {
            setMargins(this.leftMargin, contentTopMargin, this.rightMargin, this.bottomMargin)
        }

        // 4.1 Позиционирование CardView с логотипом с таким же верхним отступом, что и LinearLayout с прогнозом погоды
        cvMapLogo.layoutParams = (llForecast.layoutParams as FrameLayout.LayoutParams).apply {
            setMargins(this.leftMargin, contentTopMargin, this.rightMargin, this.bottomMargin)
        }

        // 5.1 Высота картинки фона. Так как эта картинка находится в FrameLayout слайдера,
        // то выставив её высоту достаточно большой мы определяем самый высокий элемент внутри CardView
        // и по его высоте будет регулироться высота остальных sibling элементов этой карточки.
        ivNatureBg.layoutParams.height = screenRatioHeight(0.6f)
    }

    // Управление видимостью виджетов внутри BottomSheet
    override fun setBottomSheetVisibility(hideContent: Boolean) {
        if (hideContent) {
            cvMapLogo.visibility = View.VISIBLE
            ivMapLogo.setBackgroundResource(R.drawable.crazy_marker)
            (ivMapLogo.background as AnimationDrawable).start()
        } else {
            cvMapLogo.visibility = View.GONE
            (ivMapLogo.background as AnimationDrawable).stop()
        }
    }

    // Определить высоту AppBar'а
    // https://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height/18427819#18427819
    private fun getActionBarHeight(): Int? {
        var actionBarHeight = supportActionBar?.height

        if (actionBarHeight == 0) {
            val tv = TypedValue()
            if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        return actionBarHeight
    }
    //endregion


    //region companion object
    companion object {
        private val EXTRA_MAP = MapActivity::class.java.simpleName + "extra.MAP"
        private const val REQUEST_PERM_LOCATION = 101
        private val FORECAST_KEY = "FORECAST_KEY"

        // В связи с тем, что данные будут меняться при каждом
        // запросе погоды, то используем MutableList
        private var hourly: MutableList<CustomForecast> = mutableListOf()

        // Картинки природы для фона
        private val seasonImages: MutableList<Drawable?> = mutableListOf()

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

        fun random(until: Int) = Random.nextInt(0, until)
    }
    //endregion
}