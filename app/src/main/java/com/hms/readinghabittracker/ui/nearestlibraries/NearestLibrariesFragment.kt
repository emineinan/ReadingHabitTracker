package com.hms.readinghabittracker.ui.nearestlibraries

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.data.model.Library
import com.hms.readinghabittracker.databinding.FragmentNearestLibrariesBinding
import com.hms.readinghabittracker.utils.Constant.MAP_KEY
import com.hms.readinghabittracker.utils.PermissionUtils
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationCallback
import com.huawei.hms.location.LocationRequest
import com.huawei.hms.location.LocationResult
import com.huawei.hms.location.LocationServices
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.HwLocationType
import com.huawei.hms.site.api.model.NearbySearchRequest
import com.huawei.hms.site.api.model.NearbySearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.net.URLEncoder

@AndroidEntryPoint
class NearestLibrariesFragment :
    BaseFragment<FragmentNearestLibrariesBinding, NearestLibrariesViewModel>(
        FragmentNearestLibrariesBinding::inflate
    ), EasyPermissions.PermissionCallbacks {

    override val viewModel: NearestLibrariesViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var libraryList = arrayListOf<Library>()
    private lateinit var adapter: NearestLibrariesAdapter

    private var mBinding: FragmentNearestLibrariesBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentNearestLibrariesBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun setupUi() {
        super.setupUi()
        requestPermissions()
        setAdapter()
    }

    private fun getLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()) //x
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLatitude = locationResult.lastLocation.latitude
                currentLongitude = locationResult.lastLocation.longitude
                fetchNearestLibraries()
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

    private fun fetchNearestLibraries() {
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
                MAP_KEY,
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
                    libraryList.clear()
                    siteLibraryList = it.sites
                    siteLibraryList.forEach { siteLibrary ->
                        if (siteLibrary.location != null) {
                            val library = Library(
                                siteLibrary.name,
                                siteLibrary.formatAddress,
                                siteLibrary.location
                            )
                            libraryList.add(library)
                        }
                    }
                    if (siteLibraryList.isNotEmpty()) {
                        updateList()

                        mBinding?.progressBarNearestLibraries?.visibility = View.GONE

                    } else {
                        //todo show error toast
                    }
                }
            }
        })
    }

    private fun updateList() {
        adapter.notifyDataSetChanged()
    }

    private fun setAdapter() {
        adapter = NearestLibrariesAdapter(libraryList)
        mBinding?.recyclerViewNearestLibraries?.adapter = adapter
        mBinding?.recyclerViewNearestLibraries?.setHasFixedSize(true)
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
        getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}