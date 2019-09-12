package k.s.yarlykov.travelmeteo.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.data.domain.*
import k.s.yarlykov.travelmeteo.di.AppExtensionProvider
import k.s.yarlykov.travelmeteo.di.AppExtensions
import k.s.yarlykov.travelmeteo.extensions.*
import k.s.yarlykov.travelmeteo.presenters.IMapPresenter
import k.s.yarlykov.travelmeteo.presenters.IMapView
import k.s.yarlykov.travelmeteo.presenters.MapPresenter
import kotlinx.android.synthetic.main.activity_google_map.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_forecast.*
import kotlin.random.Random

class MapActivity : AppCompatActivity(), /*OnMapReadyCallback, ForecastConsumer,*/ IMapView {

    lateinit var locationManager: LocationManager
    lateinit var markers: MutableList<Marker>

    lateinit var appExtensions: AppExtensions
    lateinit var obsLocationPermissions: Observable<Boolean>


    private var lastForecastData: CustomForecastModel? = null
    private var googleMap: GoogleMap? = null
    private var isPermissionGranted = false
    private var isLandscape: Boolean = false
    private var savedState: Bundle? = null
    private var presenter: IMapPresenter? = null

    private val compositeDisposable = CompositeDisposable()

    //region Activity Life Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        logIt("MapActivity::onCreate()")

        super.onCreate(savedInstanceState)
        savedState = savedInstanceState

        /**
         * https://medium.com/@kosmogradsky/subject-%D0%B2-rxjs-%D0%BA%D1%80%D0%B0%D1%82%D0%BA%D0%BE%D0%B5-%D0%B2%D0%B2%D0%B5%D0%B4%D0%B5%D0%BD%D0%B8%D0%B5-c9099231be6d
         * https://habr.com/ru/post/270023/
         */

        // Эмиттер результата проверки/запроса разрешений приложения
        obsLocationPermissions = BehaviorSubject.create()
        compositeDisposable.add(obsLocationPermissions.subscribe { isPermitted ->
            if (isPermitted) {
                logIt("Location permissions granted")
                appExtensions = (this.application as AppExtensionProvider).provideAppExtension()
                presenter = MapPresenter(this, appExtensions.getWeatherProviderRx())
                presenter?.onCreate()
            }
        })

        requestLocationPermissions()
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        presenter?.onDestroy()
    }

    // Сохранить последний прогноз
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(FORECAST_KEY, lastForecastData)
    }
    //endregion

    //region Options Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId ?: 0) {
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
        logIt("MapActivity::requestLocationPermissions()")
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
            (obsLocationPermissions as BehaviorSubject<Boolean>).onNext(true)
        }
    }

    // Обработка результата запроса
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERM_LOCATION -> {
                if (grantResults.size == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)
                ) {
                    recreate()
                }
            }
        }
    }
    //endregion


    //region IMapView implementation. BottomSheet Management. Update forecast info.
    /**
     * Это Observable для загрузки карты
     * https://github.com/sdoward/RxGoogleMaps/blob/master/rxgooglemap/src/main/java/com/sdoward/rxgooglemap/MapObservableProvider.java
     */
    override fun loadMap() {
        logIt("MapActivity::loadMap()")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        compositeDisposable.add(
            Single.create<GoogleMap> { emitter ->

                val mapReadyCallback = OnMapReadyCallback { map ->
                    logIt("MapActivity: OnMapReadyCallback() ${map != null}")

                    googleMap = map
                    initMap()
                    emitter.onSuccess(map)
                }
                mapFragment.getMapAsync(mapReadyCallback)
            }.subscribe{_ -> presenter?.onMapLoaded()}
        )
    }

    // Инициализация вьюшек
    override fun initViews() {
        logIt("MapActivity::initViews()")

        // Очистить список с прогнозами
        hourly.clear()
        // Список маркеров на карте
        markers = mutableListOf()
        // Определить ориентацию экрана
        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        // Загрузить нужный макет
        setContentView(if (isLandscape) R.layout.activity_google_map_lan else R.layout.activity_google_map)
        // Добавить AppBar
        setSupportActionBar(bottom_app_bar)

        // Извлечение данных из savedState
        savedState?.let {
            presenter?.onSavedDataPresent(it.getParcelable(FORECAST_KEY) as? CustomForecastModel)
        }

        // Скрыть шторку BottomSheet, потому что карта ещё не загружена
//        setBottomSheetVisibility(false)

        // Инициализация RecycleView
        rvHourly.apply {
            // Размер RV не зависит от изменения размеров его элементов
            setHasFixedSize(true)
            // Горизонтальная прокрутка
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@MapActivity,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = HourlyRVAdapter(hourly, applicationContext)
            itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        }

        // Сообщить презентеру, что все виджеты инициализированы
        presenter?.onActivityLayoutLoaded()
    }

    /**
     * Materials:
     * https://medium.com/material-design-in-action/implementing-bottomappbar-behavior-fbfbc3a30568
     */
    // Установить положение шторки: свернута или выдвинута на высоту контента
    override fun setBottomSheetState(state: Int) {
        BottomSheetBehavior.from(bottomSheet).state = state
    }

    // ????????????????
    override fun setBottomSheetSizing() {
        setBottomSheetBackground(Season.WINTER, DayPart.DAY)
    }

    // Управление видимостью BottomSheet
    override fun setBottomSheetVisibility(isVisible: Boolean) {
        bottomSheet.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Установка фона под прогнозом
    fun setBottomSheetBackground(season: Season, dayPart: DayPart) {

        // Массив с идентификаторами ресурсов картинок для текущего времени года
        val imagesId: List<Int> = appExtensions.getSeasonBackground(season)

        // Выбрать картинку в соответствии с текущим временем дня. Картинки для облачной погоды
        // пока не используем, поэтому индексы через 1.
        val idx = when (dayPart) {
            DayPart.SUNRISE -> 0
            DayPart.DAY -> 2
            else -> 4
        }

        // Отрисовать картинку фона
        with(ivNatureBg) {
            loadAsForecastBackground(imagesId[idx], dayPart, if (isLandscape) 3f else 5f)
        }
    }

    // Обновить контент в BottomSheet новыми данными
    override fun updateForecastData(model: CustomForecastModel?) {
        model?.let { m ->
            // Сохранить последний прогноз
            lastForecastData = m

            m.list.let {
                // Установить название места
                tvCity.text = m.city
                // Установить иконку погоды
                ivBkn.loadWithPicasso(this.iconId(it[0].icon), EmptyTransformation, 0f)

                // Установить температуру
                tvTemperature.text = it[0].celsius(it[0].temp)
                // Обновить RecycleView
                hourly.initFromModel(it)
                rvHourly.adapter?.notifyDataSetChanged()
                // Сменить видимость виджетов
//                setBottomSheetVisibility(lastForecastData == null)
                // Выдвинуть шторку с виджетом
                setBottomSheetState(STATE_EXPANDED)
                // Установить картинку фона под прогноз
                setBottomSheetBackground(m.season, m.dayPart)
            }
        }
    }

    // Определить высоту AppBar'а
    // https://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height/18427819#18427819
//    private fun getActionBarHeight(): Int {
//        var actionBarHeight = 0
//
//        supportActionBar?.let {
//            actionBarHeight = it.height
//
//            if (actionBarHeight == 0) {
//                val tv = TypedValue()
//                if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
//                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
//            }
//        }
//        return actionBarHeight
//    }
    //endregion

    private fun initMap() {
        logIt("MapActivity: initMap()")
        googleMap?.let {
            it.uiSettings.isZoomControlsEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false
            it.setPadding(0, 0, 0, dpToPix(80.toFloat()))

            it.setOnMapClickListener { latLng ->
                setBottomSheetState(STATE_COLLAPSED)
                markers.deleteAll()
                presenter?.onMapClick(latLng)
            }

            // Долгое нажатие на карту - запросить прогноз
            it.setOnMapLongClickListener { latLng ->
                // Запрос почасового прогноза для данной точки
                presenter?.onMapLongClick(latLng)

                // Пометить точку маркером на карте
                // https://developers.google.com/maps/documentation/android-sdk/marker
                addMarker(latLng)
            }

            @SuppressLint("MissingPermission")
            it.isMyLocationEnabled = isPermissionGranted
        }
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


    //region companion object
    companion object {
        private val EXTRA_MAP = MapActivity::class.java.simpleName + "extra.MAP"
        private const val REQUEST_PERM_LOCATION = 101
        private const val FORECAST_KEY = "FORECAST_KEY"

        // В связи с тем, что данные будут меняться при каждом
        // запросе погоды, то используем MutableList
        private var hourly: MutableList<CustomForecast> = mutableListOf()

        fun start(context: Context?, extraData: String) {
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra(EXTRA_MAP, extraData)
            }
            context?.startActivity(intent)
        }

        fun logIt(message: String?) {
            val TAG = "LogApp"
            message?.let {
                Log.e(TAG, it)
            }
        }

        fun random(until: Int) = Random.nextInt(0, until)
    }
    //endregion
}