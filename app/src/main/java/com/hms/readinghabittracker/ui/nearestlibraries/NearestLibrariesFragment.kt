package com.hms.readinghabittracker.ui.nearestlibraries

import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentNearestLibrariesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearestLibrariesFragment :
    BaseFragment<FragmentNearestLibrariesBinding, NearestLibrariesViewModel>(
        FragmentNearestLibrariesBinding::inflate
    ) {
    override val viewModel: NearestLibrariesViewModel by viewModels()

    override fun setupUi() {
        super.setupUi()
    }
}