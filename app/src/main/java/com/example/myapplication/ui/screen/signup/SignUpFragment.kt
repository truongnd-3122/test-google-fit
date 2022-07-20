package com.example.myapplication.ui.screen.signup

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignUpBinding
import com.example.myapplication.ui.base.BaseFragment
import com.example.myapplication.ui.screen.main.MainFragment
import com.example.myapplication.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override val layoutId: Int = R.layout.fragment_sign_up

    override val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.signUpListener = this@SignUpFragment
        initControls()
        initListeners()

    }

    private fun initControls() {
        setUpTextView()
    }

    private fun initListeners() {
        viewModel.isLogin.observe(viewLifecycleOwner, this::handleNavigateMainScreen)
        viewModel.errorsLiveData.observe(viewLifecycleOwner, this::handleError)
    }

    fun viewParentClick() {
        with(viewBinding) {
            viewParent.hideKeyboard()
            etEmail.clearFocus(true)
            etPassword.clearFocus(true)
            etRePassword.clearFocus(true)
        }
    }

    fun ivBackClick() {
        popBackStack()
    }

    fun handleNavigateSignInScreen() {
        ivBackClick()
    }

    fun btnSignUpClick() {
        viewParentClick()
        viewModel.validateInput()
    }

    private fun handleNavigateMainScreen(isLogin: Boolean) {
        if (isLogin) {
            handleLoading(false)
            viewBinding.tvError.text = ""
            replaceFragment(MainFragment.newInstance(), R.id.container, false)
        }
    }

    private fun handleError(error: String) {
        handleLoading(false)
        viewBinding.tvError.text = error
    }

    private fun setUpTextView(){
        val text = SpannableString(getString(R.string.string_navigate_sign_in))
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.color_cyan_light)), 16, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {
                handleNavigateSignInScreen()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        text.setSpan(clickableSpan, 16, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val policy = SpannableString(getString(R.string.string_policy))
        policy.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.color_hint_edit_text)), 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val privacyPolicyClick = object :ClickableSpan(){
            override fun onClick(p0: View) {
                val bsdf = AppBottomSheetDialogFragment()
                bsdf.isPolicy(true)
                bsdf.show(parentFragmentManager, bsdf.tag)
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }

        val termOfUseClick = object :ClickableSpan(){
            override fun onClick(p0: View) {
                val bsdf = AppBottomSheetDialogFragment()
                bsdf.isPolicy(false)
                bsdf.show(parentFragmentManager, bsdf.tag)
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }

        policy.setSpan(privacyPolicyClick, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        policy.setSpan(termOfUseClick, 11, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        with(viewBinding){
            tvSignIn.text = text
            tvSignIn.movementMethod = LinkMovementMethod.getInstance()
            tvPolicy.text = policy
            tvPolicy.movementMethod = LinkMovementMethod.getInstance()
        }
    }


}