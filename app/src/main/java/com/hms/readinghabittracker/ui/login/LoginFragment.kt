package com.hms.readinghabittracker.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentLoginBinding
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var service: HuaweiIdAuthService

    @OptIn(ExperimentalCoroutinesApi::class)
    private var signInWithHuaweiID =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.signInUserAndSaveToCloud(result.data)
            }
        }

    override fun setupListener() {
        binding.buttonLogin.setOnClickListener {
            signInWithHuaweiID.launch(service.signInIntent)
        }
    }


    override fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginUiState.collect { loginUiState ->
                    loginUiState.error.firstOrNull()?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                    loginUiState.isUSerSigned.firstOrNull()?.let {
                        findNavController().navigate(R.id.action_loginFragment_to_myBooksFragment)
                    }
                }
            }
        }
    }
}