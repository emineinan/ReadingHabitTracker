package com.hms.readinghabittracker.ui.collections

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.Collection
import com.hms.readinghabittracker.databinding.CollectionItemBinding

class CollectionsAdapter() :
    RecyclerView.Adapter<CollectionsAdapter.CollectionViewHolder>() {
    private var collections= listOf<Collection>()

    class CollectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CollectionItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.binding.apply {
            val collection = collections[position]
            textViewCollectionName.text = collection.name
        }
    }

    override fun getItemCount() = collections.size

    @SuppressLint("NotifyDataSetChanged")
    fun setCollectionList(collectionList: List<Collection>) {
        this.collections = collectionList
        notifyDataSetChanged()
    }
}