package com.example.myapplication.ui.screen.signin

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignInBinding
import com.example.myapplication.ui.base.BaseFragment
import com.example.myapplication.ui.screen.main.MainFragment
import com.example.myapplication.ui.screen.signup.SignUpFragment
import com.example.myapplication.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    override val layoutId: Int = R.layout.fragment_sign_in

    override val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.isTokenExists()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initControls()
        initListeners()
    }

    private fun initControls() {
        viewBinding.signInListener = this@SignInFragment
        setUpTextViewBottom()

    }

    private fun initListeners() {
        viewModel.isLogin.observe(viewLifecycleOwner, this::handleNavigateMainScreen)
        viewModel.errorsLiveData.observe(viewLifecycleOwner, this::handleError)
        viewModel.isTokenUser.observe(viewLifecycleOwner, this::handleNavigateMainScreen)
    }

    fun viewParentClick() {
        with(viewBinding) {
            viewParent.hideKeyboard()
            etEmail.clearFocus(true)
            etPassword.clearFocus(true)
        }
    }

    fun btnSignInClick() {
        val e = viewBinding.etEmail.text.toString()
        val p = viewBinding.etPassword.text.toString()
        viewModel.validateInput(e, p)
    }

    private fun setUpTextViewBottom(){
        val text = SpannableString(getString(R.string.string_navigate_sign_up))
        text.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.color_cyan_light)), 19, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                handleNavigateSignUpScreen()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        text.setSpan(clickableSpan, 19, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        with(viewBinding){
            tvSignUp.text = text
            tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    fun handleNavigateSignUpScreen() {
        addFragment(SignUpFragment.newInstance(), R.id.container,true)
    }

    private fun handleNavigateMainScreen(isLogin: Boolean) {
        if (isLogin) {
            handleLoading(false)
            replaceFragment(MainFragment.newInstance(), R.id.container, false)
        }
    }

    private fun handleError(error: String) {
        handleLoading(false)
        viewBinding.tvError.text = error
    }


}