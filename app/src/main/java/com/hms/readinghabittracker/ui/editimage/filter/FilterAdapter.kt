package com.hms.readinghabittracker.ui.editimage.filter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.databinding.FilterItemBinding
import com.hms.readinghabittracker.utils.listener.OnItemClickListener
import com.hms.readinghabittracker.utils.extensions.loadImage

class FilterAdapter(
    private var filteredImages: List<Bitmap>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    class FilterViewHolder(val binding: FilterItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            FilterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.binding.apply {
            val filteredImage = filteredImages[position]

            imageViewFilteredImage.loadImage(filteredImage)

            imageViewFilteredImage.setOnClickListener {
                onItemClickListener.onItemClicked(position)
            }
        }
    }

    fun updateList(newFilteredImages: List<Bitmap>) {
        filteredImages = newFilteredImages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredImages.size
    }
}