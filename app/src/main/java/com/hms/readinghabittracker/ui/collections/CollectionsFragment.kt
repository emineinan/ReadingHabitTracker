package com.hms.readinghabittracker.ui.collections

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.AddCollectionDialogBinding
import com.hms.readinghabittracker.databinding.FragmentCollectionsBinding

class CollectionsFragment :
    BaseFragment<FragmentCollectionsBinding, CollectionsViewModel>(FragmentCollectionsBinding::inflate) {
    override val viewModel: CollectionsViewModel by viewModels()

    override fun setupUi() {
        binding.fabAddCollection.setOnClickListener {
            showAddCollectionDialog()
        }
    }

    private fun showAddCollectionDialog() {
        val dialogBinding: AddCollectionDialogBinding =
            AddCollectionDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).setView(dialogBinding.root).show()
        val addCollection = dialogBinding.buttonAddCollection
        val collectionName = dialogBinding.editTextCollectionName

        addCollection.setOnClickListener {
            val title = collectionName.text.toString()
            if (title.isEmpty()) {
                Toast.makeText(context, R.string.empty_field_warning, Toast.LENGTH_SHORT).show()
            } else {
                // Add collection to CloudDb
                builder.dismiss()
            }
        }
    }
}