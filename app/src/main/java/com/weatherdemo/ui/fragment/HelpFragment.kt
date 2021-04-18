package com.weatherdemo.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import com.weatherdemo.R

import com.weatherdemo.base.BaseFragment
import com.weatherdemo.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_help.*
import kotlinx.android.synthetic.main.toolbar_center.*

class HelpFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_help
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle()
        ivMenu.setOnClickListener {
            (activity as MainActivity).openCloseDrawer()
        }

        webView.settings.javaScriptEnabled = true

        webView.loadData("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h2>Weather Demo</h2>\n" +
                "<br>\n" +
                "   <div>\n" +
                "    <h3>Home Screen</h3>\n" +
                "    <p> Here in Home screen we have list of bookmarked cities and by taping on particular city we can see today's forecast with all the information. And also we can remove city from bookmarked.</p>\n" +
                "  </div>\n" +
                "  <div>\n" +
                "    <h3>Map Screen</h3>\n" +
                "    <p> Here in Map screen we have google Map. By taping on map we can set particular city. And we have search city functionality also. And by taping on save to bookmark button we can bookmark the city.</p>\n" +
                "  </div>\n" +
                "  <div>\n" +
                "    <h3>City Detail Screen</h3>\n" +
                "    <p> Here in City Detail screen we have Today's temperature, Humidity, Wind, Rain Probability, Max and Min temperature. And we have five day forecast as well.</p>\n" +
                "  </div>\n" +
                "  <div>\n" +
                "    <h3>Setting Screen</h3>\n" +
                "    <p> Here in Setting screen we have temperature unit setting and also we can reset all bookmarked city.</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>", "text/html", "UTF-8");
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.help)
    }
}