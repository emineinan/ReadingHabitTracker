package com.hms.readinghabittracker.ui.mybooks

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.base.BaseFragment
import com.hms.readinghabittracker.databinding.FragmentMyBooksBinding

class MyBooksFragment :
    BaseFragment<FragmentMyBooksBinding, MyBooksViewModel>(FragmentMyBooksBinding::inflate) {

    override val viewModel: MyBooksViewModel by viewModels()

    override fun setupUi() {
        setHasOptionsMenu(true)
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
                // Navigate to Collections Fragment
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}