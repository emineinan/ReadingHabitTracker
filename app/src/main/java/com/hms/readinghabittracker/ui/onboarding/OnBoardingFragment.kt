package com.hms.readinghabittracker.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.data.model.OnBoardingItem
import com.hms.readinghabittracker.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {
    private lateinit var binding: FragmentOnBoardingBinding
    private val viewModel: OnBoardingViewModel by viewModels()
    private val onBoardingAdapter by lazy {
        OnBoardingAdapter(
            listOf(
                OnBoardingItem(
                    getString(R.string.on_boarding_description1),
                    R.drawable.book
                ),
                OnBoardingItem(
                    getString(R.string.on_boarding_description2),
                    R.drawable.goal
                ),
                OnBoardingItem(
                    getString(R.string.on_boarding_description3),
                    R.drawable.reader
                )
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerSlider.adapter = onBoardingAdapter
        binding.indicator.setViewPager(binding.viewPagerSlider)
        binding.viewPagerSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onBoardingAdapter.itemCount - 1) {
                    binding.buttonNext.text =
                        getString(R.string.finish)
                    binding.buttonNext.setOnClickListener {
                        findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
                        viewModel.saveOnBoardingShowedStatus(true)
                    }
                } else {
                    binding.buttonNext.text = getString(R.string.next)
                    binding.buttonNext.setOnClickListener {
                        binding.viewPagerSlider.currentItem.let {
                            binding.viewPagerSlider.setCurrentItem(it + 1, false)
                        }
                    }
                }
            }
        })
    }
}