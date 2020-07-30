package de.ude.sliot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView

class AboutActivity : AppCompatActivity() {

    internal var expandableListView: ExpandableListView? = null
    internal var adapter: ExpandableListAdapter? = null
    internal var titleList: List<String>? = null

    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val developers = ArrayList<String>()
            developers.add("Fabian Angenendt")
            developers.add("Reza Asif")
            developers.add("Constanze Becker")
            developers.add("Alexander Brehmer")
            developers.add("Dogan Günes")
            developers.add("Daniel Heghmanns")
            developers.add("Alexander Hochhalter")
            developers.add("Erik Jakobs")
            developers.add("Malte Josten")
            developers.add("Timo Matt")
            developers.add("Timo Saunus")
            developers.add("Matthias Schweiger")
            developers.add("Bernd Wosch")
            developers.add("Jule Zirger")

            val licenses = ArrayList<String>()
            licenses.add("Icon (Loudspeaker) designed by <a href=\"https://www.flaticon.com/authors/dave-gandy\">Dave Gandy</a> from <a href=\"https://www.flaticon.com/\">Flaticon</a>")
            licenses.add("Icon (Humidity) designed by <a href=\"https://www.flaticon.com/authors/kirill-kazachek\">Kirill Kazachek</a> from <a href=\"https://www.flaticon.com/\">Flaticon</a>")
            licenses.add("Icon (Temperature) designed by <a href=\"https://www.flaticon.com/authors/good-ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\">Flaticon</a>")
            licenses.add("Icon (WiFi) designed by <a href=\"https://www.flaticon.com/authors/gregor-cresnar\">Gregor Cresnar</a> from <a href=\"https://www.flaticon.com/\">Flaticon</a>")
            licenses.add(
                "Copyright 2019<br><br>" +
                "<b>Praxisprojektteam Smarte Lernraumsuche mit IOT</b><br>" +
                "<br>" +
                "Licensed under the Apache License, Version 2.0 (the \"License\"); " +
                "you may not use this file except in compliance with the License.<br><br>" +
                "You may obtain a copy of the License at<br>" +
                "<br>" +
                "<div style=\"text-align:center\"><a href=\"http://www.apache.org/licenses/LICENSE-2.0\">http://www.apache.org/licenses/LICENSE-2.0<a/></div><br>" +
                "Unless required by applicable law or agreed to in writing, software " +
                "distributed under the License is distributed on an \"AS IS\" BASIS, " +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br><br>" +
                "See the License for the specific language governing permissions and " +
                "limitations under the License. "
            )
            licenses.add("com.nfx.android:range-bar-preference:0.0.6")
            licenses.add("com.squareup.okhttp3:okhttp:4.2.1")

            listData[applicationContext.getString(R.string.caption_developers)] = developers
            listData[applicationContext.getString(R.string.caption_licenses)] = licenses

            return listData
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar!!.title = applicationContext.getString(R.string.menu_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        expandableListView = findViewById(R.id.expandableListView)
        if (expandableListView != null) {
            val listData = data

            //don't use listData.keys, because we want the same ordering for all languages!
            val titleList = arrayListOf(applicationContext.getString(R.string.caption_developers), applicationContext.getString(R.string.caption_licenses))

            adapter = AppInfoAdapter(this, titleList as ArrayList<String>, listData)
            expandableListView!!.setAdapter(adapter)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

