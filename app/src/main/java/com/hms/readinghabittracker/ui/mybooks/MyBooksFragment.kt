package com.hms.readinghabittracker.ui.mybooks

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding
import com.hms.readinghabittracker.ui.mybooks.adapter.MyBooksAdapter

class MyBooksFragment :
    BaseFragment<FragmentMyBooksBinding, MyBooksViewModel>(FragmentMyBooksBinding::inflate) {

    override val viewModel: MyBooksViewModel by viewModels()

    override fun setupUi() {
        setHasOptionsMenu(true)
        setRecyclerView()
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

    private fun setRecyclerView() {
        //binding.recyclerViewMyBooks.adapter = MyBooksAdapter(we need to our collections list)
    }
}