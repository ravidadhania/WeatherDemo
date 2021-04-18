package com.weatherdemo.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.weatherdemo.R
import com.weatherdemo.base.BaseActivity

class UiUtils {

    companion object {

        private var snackbar: Snackbar? = null
        private var toast: Toast? = null
        private var progressDialog: ProgressDialog? = null

        fun getDeviceName(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else  capitalize(manufacturer) + " " + model
        }

        private fun capitalize(str: String): String? {
            if (isEmpty(str)) {
                return str
            }
            val arr = str.toCharArray()
            var capitalizeNext = true
            val phrase = StringBuilder()
            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(Character.toUpperCase(c))
                    capitalizeNext = false
                    continue
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true
                }
                phrase.append(c)
            }
            return phrase.toString()
        }

        fun showSnackBar(context: Context?, msg: String?, positive: Boolean) {
            if (context is BaseActivity) {
                val view: View? = context.findViewById(android.R.id.content)
                showSnackBar(view, msg, positive)
            }
        }

        private fun showSnackBar(view: View?, msg: String?, positive: Boolean) {
            if (!isEmpty(msg) && view != null) {
                hideSnackBar()

                snackbar = Snackbar.make(view, msg!!, Snackbar.LENGTH_SHORT)
                val snackView = snackbar?.view
                if (positive) {
                    snackView?.setBackgroundColor(AndroidUtils.getColor(R.color.colorPrimary))
                } else {
                    snackView?.setBackgroundColor(AndroidUtils.getColor(R.color.colorAccent))
                }

                val tv: TextView? = snackView?.findViewById(R.id.snackbar_text)

                tv?.textAlignment = View.TEXT_ALIGNMENT_CENTER

                snackbar?.show()
            }
        }

        private fun hideSnackBar() {
            try {
                snackbar?.dismiss()
            } catch (e: Exception) {
                LogUtils.e(e)
            }
        }

        private fun hideToast() {
            toast?.cancel()
        }

        fun showProgressDialog(
            context: Context?,
            title: String?,
            message: String?,
            cancelable: Boolean
        ) {
            try {
                dismissProgressDialog()

                if (context == null) {
                    return
                }

                progressDialog = ProgressDialog.show(
                    context,
                    title,
                    message,
                    true,
                    cancelable
                )
            } catch (e: Exception) {
                LogUtils.e(e)
            }

        }

         fun dismissProgressDialog() {
            try {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
            } catch (e: Exception) {
                LogUtils.e(e)
            } finally {
                progressDialog = null
            }
        }
    }
}