package com.hms.readinghabittracker.ui.nearestlibraries

import android.os.Looper
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.data.model.Library
import com.hms.readinghabittracker.databinding.FragmentNearestLibrariesBinding
import com.hms.readinghabittracker.utils.PermissionUtils
import com.huawei.hms.location.*
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.net.URLEncoder

@AndroidEntryPoint
class NearestLibrariesFragment :
    BaseFragment<FragmentNearestLibrariesBinding, NearestLibrariesViewModel>(
        FragmentNearestLibrariesBinding::inflate
    ),
    EasyPermissions.PermissionCallbacks {

    override val viewModel: NearestLibrariesViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var libraryList = arrayListOf<Library>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NearestLibrariesAdapter

    override fun setupUi() {
        super.setupUi()
        requestPermissions()
    }

    private fun getLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()) //x
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLatitude = locationResult.lastLocation.latitude
                currentLongitude = locationResult.lastLocation.longitude
                requestLocation()
            }
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1800000  // change the interval as needed
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {}.addOnFailureListener {}
    }

    private fun requestLocation() {
        val request = NearbySearchRequest()
        request.hwPoiType =
            HwLocationType.LIBRARY
        request.location = Coordinate(currentLatitude, currentLongitude)
        request.radius = 30000  // meter
        request.pageIndex = 1


        lateinit var siteLibraryList: List<Site>
        val searchService = SearchServiceFactory.create(
            requireContext(),
            URLEncoder.encode(
                "DAEDAAckTAdZ/aWT5I35ayZZQXYPLugtG7hgfF3wAVnK6s9XutrbTnts4KhSi3NVxIyofRq5EvzlCUR0AFGoJRoa6LtFEOB5TuJ2Vg==",
                "utf-8"
            )
        )
        searchService.nearbySearch(request, object : SearchResultListener<NearbySearchResponse> {
            override fun onSearchError(status: SearchStatus?) {
                if (status != null) {
                    Log.e("onSearchError", status.errorMessage)
                }
            }

            override fun onSearchResult(response: NearbySearchResponse?) {
                response?.let {
                    siteLibraryList = it.sites
                    siteLibraryList.forEach { siteLibrary ->
                        val library = Library(
                            siteLibrary.name,
                            siteLibrary.formatAddress,
                            siteLibrary.location
                        )
                        libraryList.add(library)
                    }
                    setAdapter()
                }
            }
        })
    }

    private fun setAdapter() {
        recyclerView = binding.recyclerViewNearestLibraries
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NearestLibrariesAdapter(libraryList)
        recyclerView.adapter = adapter
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasLocationPermissions(requireContext())) {
            getLocation()
            return
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.location_permission),
            PermissionUtils.LOCATION_REQUEST_CODE,
            PermissionUtils.ACCESS_COARSE_LOCATION,
            PermissionUtils.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.hasLocationPermissions(requireContext())) {
            getLocation()
        }
    }
}