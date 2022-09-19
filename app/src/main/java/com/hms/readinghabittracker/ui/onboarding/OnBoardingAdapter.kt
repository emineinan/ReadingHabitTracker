package com.hms.readinghabittracker.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.OnBoardingItem
import com.hms.readinghabittracker.databinding.ItemOnboardingBinding

class OnBoardingAdapter(private val onBoardingItems: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onBoardingItem: OnBoardingItem) {
            binding.textViewTitle.text = onBoardingItem.title
            binding.textViewDescription.text = onBoardingItem.description
            binding.imageViewOnBoardingItem.setAnimation(onBoardingItem.icon)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnBoardingAdapter.ViewHolder {
        val itemBinding =
            LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding, parent, false)
        return ViewHolder(ItemOnboardingBinding.bind(itemBinding))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }
}