package com.weatherdemo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weatherdemo.R
import com.weatherdemo.callbacks.IOnAPILoadingViewCallBacks
import com.weatherdemo.network.RestResponse
import com.weatherdemo.utils.AndroidUtils
import com.weatherdemo.viewmodel.ViewModelCommon
import com.weatherdemo.utils.UiUtils
import org.koin.android.viewmodel.ext.android.viewModel

abstract class BaseFragment : androidx.fragment.app.Fragment(), IOnAPILoadingViewCallBacks {

    val vmCommon: ViewModelCommon by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    abstract fun getLayoutId(): Int

    private fun showProgressDialog(title: String?, message: String?, cancelable: Boolean = false) {
        UiUtils.showProgressDialog(activity, title, message, cancelable)
    }

    private fun hideProgressDialog() {
        UiUtils.dismissProgressDialog()
    }

    fun showSnackBar(string: String?, positive: Boolean) {
        UiUtils.showSnackBar(activity, string, positive)
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

    fun <T> observationOfAPI(it: RestResponse<T>?, callBack: IResponseParser<T>?) {
        when (it?.status) {

            RestResponse.Status.LOADING -> {
                callBack?.onLoading()
            }

            RestResponse.Status.ERROR -> {
            }

            RestResponse.Status.SUCCESS -> {
                callBack?.onSuccess(it)
            }
        }
    }
}