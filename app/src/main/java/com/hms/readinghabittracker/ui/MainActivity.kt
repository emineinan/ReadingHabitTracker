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
import com.hms.readinghabittracker.ui.goals.GoalsFragmentDirections
import com.hms.readinghabittracker.ui.splash.SplashFragmentDirections
import com.hms.readinghabittracker.utils.Constant.INITIAL_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToGoalItemFragment(goalItemId)
                    navController.navigate(action)
                }
            }
        }
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