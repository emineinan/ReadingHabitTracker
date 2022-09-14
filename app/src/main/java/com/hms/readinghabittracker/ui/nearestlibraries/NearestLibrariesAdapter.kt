package com.hms.readinghabittracker.ui.nearestlibraries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.Library
import com.hms.readinghabittracker.databinding.LibraryItemBinding

class NearestLibrariesAdapter(private val libraries: List<Library>) :
    RecyclerView.Adapter<NearestLibrariesAdapter.NearestLibrariesViewHolder>() {

    class NearestLibrariesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = LibraryItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearestLibrariesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.library_item, parent, false)
        return NearestLibrariesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NearestLibrariesViewHolder, position: Int) {
        holder.binding.apply {
            val library = libraries[position]
            textViewLibraryName.text = library.name
            textViewLibraryAddress.text = library.address
        }
    }

    override fun getItemCount() = libraries.size
}