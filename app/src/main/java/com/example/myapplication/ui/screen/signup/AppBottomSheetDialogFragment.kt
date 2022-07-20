package com.example.myapplication.ui.screen.signup

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import com.example.myapplication.data.constants.Constants
import com.example.myapplication.data.remote.api.HeaderInterceptor
import com.example.myapplication.databinding.LayoutBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppBottomSheetDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: LayoutBottomSheetDialogBinding
    private lateinit var dialog: BottomSheetDialog
    private lateinit var bsb: BottomSheetBehavior<View>
    private var isPolicy: Boolean = false

    fun isPolicy(isPolicy: Boolean){
        this.isPolicy = isPolicy
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutBottomSheetDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bsb = BottomSheetBehavior.from(view.parent as View)
        bsb.state = BottomSheetBehavior.STATE_EXPANDED

        val layout = binding.layoutBottomSheet
        if (layout!= null){
            layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
        }

        val map = HashMap<String, String>()
        map[HeaderInterceptor.AUTH] = HeaderInterceptor.VALUE_AUTH

        with(binding.wv){
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            if (isPolicy)loadUrl(Constants.PRIVACY_POLICIES_URL, map)
            else loadUrl(Constants.TERM_OF_USE_URL, map)
        }

        binding.tvCompletion.setOnClickListener {
            dialog.dismiss()
        }
    }
}