package com.weatherdemo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.weatherdemo.R
import com.weatherdemo.callbacks.IOnAPILoadingViewCallBacks
import com.weatherdemo.network.RestResponse
import com.weatherdemo.utils.AndroidUtils
import com.weatherdemo.viewmodel.ViewModelCommon
import com.weatherdemo.utils.UiUtils

import org.koin.android.viewmodel.ext.android.viewModel


abstract class BaseActivity : AppCompatActivity(), IOnAPILoadingViewCallBacks {

    val vmBase: BaseViewModel<BaseRepository> by viewModel()
    val vmCommon: ViewModelCommon by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
    }

    abstract fun getLayoutId(): Int

    private fun showProgressDialog(title: String?, message: String?, cancelable: Boolean = false) {
        UiUtils.showProgressDialog(this, title, message, cancelable)
    }

    private fun hideProgressDialog() {
        UiUtils.dismissProgressDialog()
    }

    fun showSnackBar(string: String?, positive: Boolean) {
        UiUtils.showSnackBar(this, string, positive)
    }

    override fun <T> responseStatusSuccess(it: RestResponse<T>) {
        hideProgressDialog()
    }

    override fun <T> responseStatusError(it: RestResponse<T>) {
        hideProgressDialog()
        showSnackBar(it.getErrorMessage(), false)
    }

    override fun responseStatusLoading() {
        showProgressDialog(null, AndroidUtils.getString(R.string.loading))
    }
}