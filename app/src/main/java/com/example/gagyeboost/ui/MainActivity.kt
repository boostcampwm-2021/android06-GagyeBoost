package com.example.gagyeboost.ui

import android.os.Bundle
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ActivityMainBinding
import com.example.gagyeboost.ui.base.BaseActivity
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navController: NavController
    private val viewModel: AddViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBottomNavigationBar()
    }

    private fun setBottomNavigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fg_navigation_host) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.mainBottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            with(binding.mainBottomNavigation) {
                menu.forEach { it.isEnabled = false }
                postDelayed({
                    menu.forEach { it.isEnabled = true }
                }, 500)
            }
        }
    }
}
