package com.hms.readinghabittracker.ui.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentSplashBinding
import com.hms.readinghabittracker.ui.MainActivity
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(FragmentSplashBinding::inflate) {

    override val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var agConnect: AGConnectAuth

    override fun setupObserver() {
        lifecycleScope.launch {
            if (deepLinkIsExist()) {
                return@launch
            }

            delay(2000)

            if (agConnect.currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_myBooksFragment)
            } else {
                viewModel.isOnBoardingShowedFlow.collect {
                    if (it) {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
                    }
                }
            }
        }
    }


    private fun deepLinkIsExist(): Boolean {
        val activity = requireActivity() as MainActivity

        if (activity.isDeepLinkExist && activity.getDeepLinkGoalItemId() != null) {
            val goalItemId = activity.getDeepLinkGoalItemId() ?: return false
            activity.deepLinkIsHandled()
            val action =
                SplashFragmentDirections.actionSplashFragmentToGoalItemFragment(
                    goalItemId
                )
            findNavController().navigate(action)
            return true
        }
        return false
    }
}