package com.hms.readinghabittracker.ui.collections

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.AddCollectionDialogBinding
import com.hms.readinghabittracker.databinding.FragmentCollectionsBinding
import com.huawei.agconnect.auth.AGConnectAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CollectionsFragment :
    BaseFragment<FragmentCollectionsBinding, CollectionsViewModel>(FragmentCollectionsBinding::inflate) {
    override val viewModel: CollectionsViewModel by viewModels()

    @Inject
    lateinit var agConnect: AGConnectAuth

    private var collectionsAdapter = CollectionsAdapter()

    override fun setupUi() {
        binding.fabAddCollection.setOnClickListener {
            showAddCollectionDialog()
        }
        setAdapter()
    }

    private fun showAddCollectionDialog() {
        val dialogBinding: AddCollectionDialogBinding =
            AddCollectionDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).setView(dialogBinding.root).show()
        val addCollection = dialogBinding.buttonAddCollection
        val collectionName = dialogBinding.editTextCollectionName

        addCollection.setOnClickListener {
            val collectionNameText = collectionName.text.toString()
            if (collectionNameText.isEmpty()) {
                Toast.makeText(context, R.string.empty_field_warning, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveCollectionToCloudDb(agConnect, collectionNameText)
                builder.dismiss()
            }
        }
    }

    override fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectionsUiState.collect { collectionsUiState ->
                    collectionsUiState.error.firstOrNull()?.let {
                        Toast.makeText(
                            requireContext(),
                            "Collection is not saved, there is an error...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    collectionsUiState.isCollectionSaved.firstOrNull()?.let {
                        Toast.makeText(
                            requireContext(),
                            "Collection is saved successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (collectionsUiState.savedCollectionList.isNotEmpty()) {
                        collectionsUiState.savedCollectionList.let {
                            collectionsAdapter.setCollectionList(it)
                        }
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerViewCollections.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCollections.setHasFixedSize(true)
        binding.recyclerViewCollections.adapter = collectionsAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCollections(agConnect)
    }
}