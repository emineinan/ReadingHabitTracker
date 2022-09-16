package com.hms.readinghabittracker.ui.map

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMapBinding
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.MarkerOptions

class MapFragment :
    BaseFragment<FragmentMapBinding, MapViewModel>(FragmentMapBinding::inflate),
    OnMapReadyCallback {

    override val viewModel: MapViewModel by viewModels()
    private lateinit var huaweiMap: HuaweiMap
    private val args: MapFragmentArgs by navArgs()

    override fun setupUi() {
        binding.huaweiMap.apply {
            onCreate(null)
            getMapAsync(this@MapFragment)
        }
    }

    private fun setUpMapView() {
        huaweiMap.apply {
            isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isZoomControlsEnabled = true
            mapType = HuaweiMap.MAP_TYPE_NORMAL
        }
        addSiteMarker(
            LatLng(args.library.location.lat, args.library.location.lng), args.library.name
        )

        huaweiMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    args.library.location.lat,
                    args.library.location.lng
                ), 16f
            )
        )
    }

    override fun onMapReady(map: HuaweiMap) {
        huaweiMap = map
        setUpMapView()
    }

    private fun addSiteMarker(latLng: LatLng, title: String) {
        huaweiMap.apply {
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .draggable(false)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_building))
            )
        }
    }
}
