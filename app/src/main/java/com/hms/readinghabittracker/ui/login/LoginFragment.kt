package com.hms.readinghabittracker.ui.login

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentLoginBinding
import com.hms.readinghabittracker.utils.Constants
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var service: HuaweiIdAuthService

    override fun setupListener() {
        binding.buttonLogin.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        startActivityForResult(service.signInIntent, Constants.loginRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.loginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.userSignedIn(data)
                findNavController().navigate(R.id.action_loginFragment_to_myBooksFragment)
            }
        }
    }
}




