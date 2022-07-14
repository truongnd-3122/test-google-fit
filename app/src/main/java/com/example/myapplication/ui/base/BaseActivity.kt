package com.example.myapplication.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.BR
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogForceUpdateBinding
import com.example.myapplication.utils.*

abstract class BaseActivity<ViewBinding : ViewDataBinding, ViewModel : BaseViewModel> :
    AppCompatActivity() {

    protected lateinit var viewBinding: ViewBinding

    protected abstract val viewModel: ViewModel

    private var forceUpdateDialog: AlertDialog? = null

    lateinit var thresholdClickTime: ThresholdClickTime

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.apply {
            setVariable(BR.viewModel, viewModel)
            root.isClickable = true
            viewBinding.lifecycleOwner = this@BaseActivity
            executePendingBindings()
        }
        forceUpdateDialog = AlertDialog.Builder(this).setView(
            DataBindingUtil.inflate<DialogForceUpdateBinding>(
                LayoutInflater.from(this), R.layout.dialog_force_update, null, false
            ).apply {
                tvConfirm.setClickSafe {

                }
            }.root
        ).create().apply {
            setOnCancelListener {
                finish()
            }
            setCanceledOnTouchOutside(false)
        }
        viewBinding.lifecycleOwner = this
        observeErrorEvent()
    }

    protected fun observeErrorEvent() {
        viewModel.apply {
            isLoading.observe(this@BaseActivity) {
                handleLoading(it == true)
            }
            errorMessage.observe(this@BaseActivity) {
                handleErrorMessage(it)
            }
            noInternetConnectionEvent.observe(this@BaseActivity) {
                handleErrorMessage(getString(R.string.no_internet_connection))
            }
            connectTimeoutEvent.observe(this@BaseActivity) {
                handleErrorMessage(getString(R.string.connect_timeout))
            }
            forceUpdateAppEvent.observe(this@BaseActivity) {
                handleErrorMessage(getString(R.string.force_update_app))
            }
            serverMaintainEvent.observe(this@BaseActivity) {
                handleErrorMessage(getString(R.string.server_maintain_message))
            }
            unknownErrorEvent.observe(this@BaseActivity) {
                handleErrorMessage(getString(R.string.unknown_error))
            }
        }
    }

    /**
     * override this if not use loading dialog (example progress bar)
     */
    open fun handleLoading(isLoading: Boolean) {
        if (isLoading) showLoadingDialog() else dismissLLoadingDialog()
    }

    fun handleErrorMessage(message: String?) {
        if (message.isNullOrBlank()) return

        dismissLLoadingDialog()

        showDialog(
            message = message,
            textPositive = getString(R.string.ok)
        )
    }

    fun findFragment(TAG: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(TAG)
    }


    fun addFragment(
        fragment: BaseFragment<*, *>,
        containerViewId: Int,
        addToBackStack: Boolean = true,
        transit: Int = -1
    ) {
        supportFragmentManager.beginTransaction()
            .apply {
                if (addToBackStack) addToBackStack(fragment::class.simpleName)
                if (transit != FragmentTransaction.TRANSIT_NONE) setTransition(
                    FragmentTransaction.TRANSIT_NONE)
            }
            .add(containerViewId, fragment, fragment::class.simpleName)
            .commit()
    }

    fun replaceFragment(
        fragment: BaseFragment<*, *>,
        containerViewId: Int,
        addToBackStack: Boolean = true,
        transit: Int = -1
    ) {
        supportFragmentManager.beginTransaction()
            .apply {
                if (addToBackStack) addToBackStack(fragment::class.simpleName)
                if (transit != FragmentTransaction.TRANSIT_NONE) setTransition(
                    FragmentTransaction.TRANSIT_NONE)
            }
            .replace(containerViewId, fragment, fragment::class.simpleName)
            .commit()
    }

    fun backToPreviousScreen() {
        supportFragmentManager.popBackStack()
    }


    fun showDialogFragment(
        dialogFragment: DialogFragment,
        addToBackStack: Boolean = false, transit: Int = FragmentTransaction.TRANSIT_NONE
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(dialogFragment::class.simpleName)
        if (transit != FragmentTransaction.TRANSIT_NONE) transaction.setTransition(transit)
        if (transaction != null) {
            dialogFragment.show(transaction, dialogFragment::class.simpleName)
        }
    }

    fun clearBackStack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
