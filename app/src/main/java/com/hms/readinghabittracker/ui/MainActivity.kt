package com.hms.readinghabittracker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hms.readinghabittracker.R
import com.hms.readinghabittracker.databinding.ActivityMainBinding
import com.hms.readinghabittracker.ui.goals.GoalItemFragment
import com.hms.readinghabittracker.utils.Constant.INITIAL_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var isDeepLinkExist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationView
        val navController by lazy {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment

            navHostFragment.navController
        }

        bottomNavigationView.setupWithNavController(navController)
        setUpBottomNavigationView(navController)


        val initialFragment = intent.getStringExtra(INITIAL_FRAGMENT)
        initialFragment?.let {
            if (it == GoalItemFragment.TAG) {
                val goalItemId = this@MainActivity.intent.getIntExtra(GoalItemFragment.ID, -1)
                if (goalItemId >= 0) {
                    isDeepLinkExist = true
                }
            }
        }

    }

    fun deepLinkIsHandled() {
        isDeepLinkExist = false
    }

    fun getDeepLinkGoalItemId(): Int? {
        if (!isDeepLinkExist) {
            return null
        }
        val goalItemId = this@MainActivity.intent.getIntExtra(GoalItemFragment.ID, -1)
        if (goalItemId == -1) {
            return null
        }
        return goalItemId
    }

    private fun setUpBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.myBooksFragment -> showBottomNavigationView()
                R.id.goalsFragment -> showBottomNavigationView()
                else -> hideBottomNavigationView()
            }
        }
    }

    private fun showBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }
}