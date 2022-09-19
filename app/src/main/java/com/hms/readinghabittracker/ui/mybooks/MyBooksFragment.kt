package com.hms.readinghabittracker.ui.mybooks

import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding
import com.hms.readinghabittracker.ui.mybooks.adapter.MyBooksAdapter
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyBooksFragment :
    BaseFragment<FragmentMyBooksBinding, MyBooksViewModel>(FragmentMyBooksBinding::inflate) {

    override val viewModel: MyBooksViewModel by viewModels()

    @Inject
    lateinit var agConnect: AGConnectAuth

    private var myBooksAdapter = MyBooksAdapter()

    override fun setupUi() {
        setAdapter()
        binding.fabCollections.setOnClickListener {
            findNavController().navigate(R.id.action_myBooksFragment_to_collectionsFragment)
        }
        viewModel.getCollectionsForCurrentUser()
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myBooksUiState.collect { myBooksUiState ->
                    binding.loadingBar.isVisible = myBooksUiState.loading

                    binding.tvErrorMsg.isVisible = myBooksUiState.showEmptyListMessage

                    if (myBooksUiState.collectionsAndBooks.isNotEmpty()) {
                        myBooksUiState.collectionsAndBooks.let {
                            Log.d("CollectionsAndBooks", it.toString())
                            myBooksAdapter.setCollectionList(it)
                        }
                    }

                    if (myBooksUiState.error.isNotBlank()) {
                        Toast.makeText(requireContext(), "${myBooksUiState.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setAdapter() {
        binding.recyclerViewMyBooks.setHasFixedSize(true)
        binding.recyclerViewMyBooks.adapter = myBooksAdapter
        val freeSpaceAtBottom = 100 // the bottom free space in pixels
        binding.recyclerViewMyBooks.clipToPadding = false
        binding.recyclerViewMyBooks.setPadding(binding.recyclerViewMyBooks.paddingLeft,
            binding.recyclerViewMyBooks.top,
            binding.recyclerViewMyBooks.right,
            freeSpaceAtBottom)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCollectionsForCurrentUser()
    }
}