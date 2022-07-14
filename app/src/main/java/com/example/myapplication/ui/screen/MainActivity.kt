package com.example.myapplication.ui.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.base.BaseActivity
import com.example.myapplication.ui.screen.signin.SignInFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

    override val viewModel: MainActivityViewModel by viewModels()
    override val layoutId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(SignInFragment.newInstance(), R.id.container, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("sss", resultCode.toString())
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}
