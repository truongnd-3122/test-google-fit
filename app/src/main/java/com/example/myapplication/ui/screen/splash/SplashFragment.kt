package com.example.myapplication.ui.screen.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSplashBinding
import com.example.myapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override val layoutId: Int = R.layout.fragment_splash

    override val viewModel: SplashViewModel by viewModels {
        SavedStateViewModelFactory(requireActivity().application, this)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            delay(1000)
            navigateToMain()
        }
    }

    private fun navigateToMain() {
//        getNavController()?.navigate(SplashFragmentDirections.toMain())
    }
}
