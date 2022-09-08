package com.hms.readinghabittracker.ui.mybooks

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding
import com.hms.readinghabittracker.ui.mybooks.adapter.MyBooksAdapter
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyBooksFragment() :
    BaseFragment<FragmentMyBooksBinding, MyBooksViewModel>(FragmentMyBooksBinding::inflate) {

    override val viewModel: MyBooksViewModel by viewModels()

    @Inject
    lateinit var agConnect: AGConnectAuth

    private var myBooksAdapter = MyBooksAdapter()

    override fun setupUi() {
        setHasOptionsMenu(true)
        setAdapter()
        viewModel.getCollectionsForCurrentUser()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_books_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                // Navigate to Add Book Fragment
                true
            }
            R.id.action_collections -> {
                findNavController().navigate(R.id.action_myBooksFragment_to_collectionsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myBooksUiState.collect { myBooksUiState ->
                    myBooksUiState.loading.let {
                        Toast.makeText(
                            requireContext(),
                            "Books are loading...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (myBooksUiState.collectionsAndBooks.isNotEmpty()) {
                        myBooksUiState.collectionsAndBooks.let {
                            Log.d("CollectionsAndBooks", it.toString())
                            myBooksAdapter.setCollectionList(it)
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerViewMyBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyBooks.setHasFixedSize(true)
        binding.recyclerViewMyBooks.adapter = myBooksAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCollectionsForCurrentUser()
    }
}