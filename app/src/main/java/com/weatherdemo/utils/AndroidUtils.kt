package com.weatherdemo.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.weatherdemo.base.BaseApplication
import com.weatherdemo.constants.MyConfig
import java.text.SimpleDateFormat
import java.util.*

class AndroidUtils {

    companion object {

        fun requestRuntimePermission(
            activity: Activity?,
            title: String?,
            message: String?,
            permissions: Array<String>,
            requestCode: Int
        ): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var isAllpermissionGranted = true
                var shouldShowPermissionalRationale = false
                val permissionNotGranted: MutableList<String> =
                    ArrayList()
                for (i in permissions.indices) {
                    if (ContextCompat.checkSelfPermission(
                            activity!!,
                            permissions[i]
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        isAllpermissionGranted = false
                        permissionNotGranted.add(permissions[i])
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permissions[i]
                        )
                    ) {
                        shouldShowPermissionalRationale = true
                    }
                }
                if (isAllpermissionGranted) {
                    return true
                }
                if (shouldShowPermissionalRationale) {
                    val builder: AlertDialog.Builder
                    builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AlertDialog.Builder(
                            activity,
                            android.R.style.ThemeOverlay_Material_Dialog_Alert
                        )
                    } else {
                        AlertDialog.Builder(activity)
                    }
                    builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(
                            android.R.string.yes
                        ) { dialog, which ->
                            ActivityCompat.requestPermissions(
                                activity!!,
                                permissionNotGranted.toTypedArray(),
                                requestCode
                            )
                        }
                        .setNegativeButton(
                            android.R.string.no
                        ) { dialog, which ->

                        }
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        permissionNotGranted.toTypedArray(),
                        requestCode
                    )
                }
                false
            } else {
                true
            }
        }

        fun isNetworkAvailable(mContext: Context): Boolean {

            /* getting systems Service connectivity manager */
            val mConnectivityManager = mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfos =
                mConnectivityManager.allNetworkInfo
            for (i in mNetworkInfos.indices) {
                if (mNetworkInfos[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }

        fun replaceFragment(
            fragmentManager: androidx.fragment.app.FragmentManager?, @IdRes id: Int,
            fragment: androidx.fragment.app.Fragment,
            tag: String = fragment::class.java.name
        ) {
            Log.i("TAGTAG", "TAG " + tag)
            fragmentManager?.beginTransaction()
                ?.replace(id, fragment, tag)
                ?.commitAllowingStateLoss()
        }

        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager = activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                if (activity.currentFocus != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus!!.windowToken, 0
                    )
                }
            }
        }

        @JvmStatic
        fun getString(@StringRes id: Int, vararg objects: Any?) = if (objects.isEmpty()) {
            BaseApplication.getInstance().resources.getString(id)
        } else {
            BaseApplication.getInstance().resources.getString(id, *objects)
        }

        fun getColor(@ColorRes id: Int) =
            ContextCompat.getColor(BaseApplication.getInstance(), id)

        fun convertToTimeFormat(
            fromDateFormat: String? = MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
            toDateFormat: String,
            dateValue: String?
        ): String {
            return try {
                val formatFrom = SimpleDateFormat(
                    fromDateFormat, Locale.US
                )
                val date = formatFrom.parse(dateValue)
                val formatTo = SimpleDateFormat(
                    toDateFormat, Locale.US
                )

                formatTo.timeZone = TimeZone.getDefault()
                return formatTo.format(date)

            } catch (e: Exception) {
                ""
            }
        }

        fun getDayFromDate(
            fromDateFormat: String = MyConfig.DateFormat.yyyy_MM_dd_HH_mm_ss,
            toDateFormat: String = MyConfig.DateFormat.EEEE,
            dateValue: String?
        ): String {
            val formatFrom = SimpleDateFormat(fromDateFormat, Locale.US)
            val date = formatFrom.parse(dateValue)
            val formatTo = SimpleDateFormat(toDateFormat, Locale.US)
            formatTo.timeZone = TimeZone.getDefault()
            return formatTo.format(date)
        }

        fun getDateWithAdditionDay(toDateFormat: String, day: Int = 0): String {
            val date = Date();
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DATE, day)
            val formatter = SimpleDateFormat(toDateFormat)
            return formatter.format(calendar.time)
        }
    }
}