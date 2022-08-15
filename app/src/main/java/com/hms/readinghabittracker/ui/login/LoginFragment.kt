package com.hms.readinghabittracker.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.databinding.FragmentLoginBinding
import com.hms.readinghabittracker.utils.Constant
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var service: HuaweiIdAuthService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.buttonLogin.setOnClickListener {
            signIn()
        }

        return binding.root
    }

    private fun signIn() {
        startActivityForResult(service.signInIntent, Constant.loginRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.loginRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.userSignedIn(data)
                findNavController().navigate(R.id.action_loginFragment_to_myBooksFragment)
            }
        }
    }
}