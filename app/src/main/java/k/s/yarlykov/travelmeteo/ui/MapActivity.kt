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
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback, /*ForecastConsumer,*/ IMapView {

    lateinit var locationManager: LocationManager
    lateinit var markers: MutableList<Marker>
    lateinit var presenter: IMapPresenter
    lateinit var appExtensions: AppExtensions
    private var lastForecastDate: CustomForecastModel? = null
    private var googleMap: GoogleMap? = null
    private var isPermissionGranted = false
    private var isLandscape: Boolean = false
    private var savedState: Bundle? = null

    //region Activity Life Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedState = savedInstanceState

        // DI. Инициализировать presenter'а
        appExtensions = (this.application as AppExtensionProvider).provideAppExtension()
        presenter = MapPresenter(this, appExtensions.getWeatherProvider())

        // Запросить разрешение на работу с гео
        requestLocationPermissions()

        // Оповестить презентера
        presenter.onCreate()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
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
            presenter.onPermissions(isPermissionGranted)
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
                    isPermissionGranted = true
                    presenter.onPermissions(isPermissionGranted)
                }
            }
        }

        if (isPermissionGranted) {
            recreate()
        }
    }
    //endregion

    //region GoogleMap Methods
    private fun initMap() {
        googleMap?.let {
            it.uiSettings.isZoomControlsEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false
            it.setPadding(0, 0, 0, dpToPix(80.toFloat()))

            it.setOnMapClickListener { latLng ->
                setBottomSheetState(STATE_COLLAPSED)
                markers.deleteAll()
                presenter.onMapClick(latLng)
            }

            // Долгое нажатие на карту - запросить прогноз
            it.setOnMapLongClickListener { latLng ->
                // Запрос почасового прогноза для данной точки
                presenter.onMapLongClick(latLng)

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

    //region IMapView implementation. BottomSheet Management. Update forecast info.
    // Инициализация вьюшек
    override fun initViews() {

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

        // Подгрузить карту асинхронно
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Скрыть шторку BottomSheet, потому что карта ещё не загружена
        setBottomSheetVisibility(hideContent = true)

        // Извлечение данных из savedState
        savedState?.let {
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

        // Сообщить презентеру, что все виджеты инициализированы
        presenter.onMapScreenReady()
    }

    /**
     * Materials:
     * https://medium.com/material-design-in-action/implementing-bottomappbar-behavior-fbfbc3a30568
     */
    // Установить положение шторки: свернута или выдвинута на высоту контента
    override fun setBottomSheetState(state: Int) {
        BottomSheetBehavior.from(bottom_sheet).state = state
    }

    // Расчитываем высоту "шапки" BottomSheet'a которая будет над BottomAppBar'ом
    override fun setBottomSheetSizing() {

        // Максимальная высота всего содержимого BottomSheet
        bottom_sheet.layoutParams.height = screenRatioHeight(0.6f)

        // Высота BottomAppBar
        val bottomAppBarHeight = getActionBarHeight()

        // 1. Расчет и установка высоты верхней видимой части BottomSheet
        // в свернутом состоянии: то что будет видно над BottomAppBar.
        // Назовем эту видимую часть sheetCap

        // 1.1. Расстояние между двумя наложенными друг на друга CardView (верхняя рамка)
        val shadowHeight = (cvNested.layoutParams as FrameLayout.LayoutParams).topMargin

        // 1.2. Высота видимой части BottomSheet
        val sheetCapHeight = with(vSlider.layoutParams as LinearLayout.LayoutParams) {
            topMargin + bottomMargin + height
        }

        // 2.1 Высота рамки поверх BottomAppBar
        val bottomAppBarBorderHeight = (vAppBarBorder.layoutParams as CoordinatorLayout.LayoutParams).height

        // 2.2 Складываем все вместе и получаем peekHeight для BottomSheet
        val calculatedPeekHeight =
            bottomAppBarHeight +
                    shadowHeight +
                    sheetCapHeight +
                    bottomAppBarBorderHeight

        // 2.3 Установка высоты "шапочки"
        BottomSheetBehavior.from(bottom_sheet).peekHeight = calculatedPeekHeight

        // 3 Позиционирование LinearLayout с прогнозом погоды внутри BottomSheet
        // 3.1 Расчет отступа контента от верхней рамки BottomSheet
        val contentTopMargin =
            shadowHeight +
                    sheetCapHeight +
                    bottomAppBarBorderHeight

        // 3.2 Установка вычисленного значения через LayoutParams
        llForecast.layoutParams = (llForecast.layoutParams as FrameLayout.LayoutParams).apply {
            setMargins(this.leftMargin, contentTopMargin, this.rightMargin, this.bottomMargin)
        }

        // 4.1 Позиционирование ImageView с картинкой фона.
        // Почему-то в портретной ориентации отрисовка фона поверх BottomAppBar, как и надо,
        // а в горизонтальной - картинка уходит вниз. Поэтому явно поднимаем её на высоту bottomAppBarHeight
        ivNatureBg.layoutParams = (ivNatureBg.layoutParams as LinearLayout.LayoutParams).apply {
            setMargins(this.leftMargin,
                contentTopMargin,
                this.rightMargin,
                if(isLandscape) bottomAppBarHeight else this.bottomMargin)
        }
    }

    // Управление видимостью виджетов внутри BottomSheet
    override fun setBottomSheetVisibility(hideContent: Boolean) {
    }

    // Установка фона под прогнозом
    fun setBottomSheetBackground(season: Season, dayPart: DayPart, dims: Pair<Int, Int>) {

        // Массив с идентификаторами ресурсов картинок для текущего времени года
        val imagesId: List<Int> = appExtensions.getSeasonBackground(season)

        // Выбрать картику в соответствии с текущим временем дня. Картинки для облачной погоды
        // пока не используем, поэтому индексы через 1.
        val r = when (dayPart) {
            DayPart.SUNRISE -> 0
            DayPart.DAY -> 2
            else -> 4
        }

        // Отрисовываем картинку фона
        with(ivNatureBg) {
            if (isLandscape) {
                // Здесь вместо dims.first и dims.second уже можно использовать
                // ivNatureBg.width и ivNatureBg.height. Значения одинаковые.
                loadAndFitWithPicasso(imagesId[r], dims.first, dims.second)
            } else {
                loadWithPicasso(imagesId[r], EmptyTransformation, 0f)
            }
        }
    }

    // Обновить контент в BottomSheet новыми данными
    override fun updateForecastData(model: CustomForecastModel?) {
        model?.let { m ->
            // Сохранить последний прогноз
            lastForecastDate = m

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
                setBottomSheetVisibility(hideContent = false)
                // Выдвинуть шторку с виджетом
                setBottomSheetState(STATE_EXPANDED)

                // Установить заливку фона

                // Установить картинку фона под прогноз
                ivNatureBg.post {
                    setBottomSheetBackground(m.season, m.dayPart, Pair(ivNatureBg.width, ivNatureBg.height))
                }
            }
        }
    }

    // Определить высоту AppBar'а
    // https://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height/18427819#18427819
    private fun getActionBarHeight(): Int {
        var actionBarHeight = 0

        supportActionBar?.let {
            actionBarHeight = it.height

            if (actionBarHeight == 0) {
                val tv = TypedValue()
                if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            }
        }
        return actionBarHeight
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
            val TAG = "MapActivity"
            message?.let {
                Log.d(TAG, it)
            }
        }

        fun random(until: Int) = Random.nextInt(0, until)
    }
    //endregion
}