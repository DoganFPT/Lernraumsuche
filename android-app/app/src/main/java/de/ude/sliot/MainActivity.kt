package de.ude.sliot

import EmptySubmitSearchView
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nfx.android.rangebarpreference.RangeBarHelper
import de.ude.sliot.data_class.FilterWeightConfiguration
import de.ude.sliot.data_class.Room
import de.ude.sliot.data_class.JSONEntry
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val rooms : ArrayList<Room> = ArrayList()
    private var sortedBy = SortEnum.RECOMMENDATION
    private var reversed = false
    private var entries: Array<JSONEntry>? = null
    private var roomFilter: String? = null
    private var isFiltering: Boolean = false
    private var filterString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create global objects
        applyDefaultValuesToEmptySettings()

        //Create a vertical Layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Set refresh handler and run the refresh initially to get our JSONData
        swipe_refresher.setOnRefreshListener {
            refreshData()
        }
    }

    private fun applyDefaultValuesToEmptySettings() {
        val defaultWeight = 5

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()) {
            //Fahrenheit
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_fahrenheit)))
                putBoolean(resources.getString(R.string.settings_key_fahrenheit), false)

            //Dark Mode
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_dark_mode)))
                putBoolean(resources.getString(R.string.settings_key_dark_mode), false)

            //Campus (B, L, M)
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_campus_b)))
                putBoolean(resources.getString(R.string.settings_key_campus_b), true)

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_campus_l)))
                putBoolean(resources.getString(R.string.settings_key_campus_l), true)

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_campus_m)))
                putBoolean(resources.getString(R.string.settings_key_campus_m), true)

            //Temperature
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_temperature)))
                putString(resources.getString(R.string.settings_key_temperature), RangeBarHelper.convertValuesToJsonString(18f, 25f))

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_temperature_weight)))
                putInt(resources.getString(R.string.settings_key_temperature_weight), defaultWeight)

            //Humidity
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_humidity)))
                putString(resources.getString(R.string.settings_key_humidity), RangeBarHelper.convertValuesToJsonString(35f, 65f))

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_humidity_weight)))
                putInt(resources.getString(R.string.settings_key_humidity_weight), defaultWeight)

            //Noise
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_noise)))
                putInt(resources.getString(R.string.settings_key_noise), 4)

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_noise_weight)))
                putInt(resources.getString(R.string.settings_key_noise_weight), defaultWeight)

            //WiFi
            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_wifi)))
                putInt(resources.getString(R.string.settings_key_wifi), 5)

            if (!sharedPreferences.contains(resources.getString(R.string.settings_key_wifi_weight)))
                putInt(resources.getString(R.string.settings_key_wifi_weight), defaultWeight)

            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_buttons, menu)

        menu?.findItem(R.id.search)?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                isFiltering = false
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                isFiltering = true
                return true
            }
        })

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.search)?.actionView as EmptySubmitSearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    clearFocus()
                    filterString = query
                    return filterData(query)
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterString = newText
                    return filterData(newText)
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsButton -> {
                startActivity(Intent(applicationContext, SettingsOverviewActivity::class.java))
                return true
            }

            R.id.sortingButton -> {
                val popupMenu = PopupMenu(this, findViewById(R.id.sortingButton))
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.sortByRecommendationButton -> { sortBy(SortEnum.RECOMMENDATION,false);true }
                        R.id.sortByRoomNameButton -> { sortBy(SortEnum.NAME,false);true }
                        R.id.sortByTemperatureButton -> { sortBy(SortEnum.TEMPERATURE,false);true }
                        R.id.sortByWiFiButton -> { sortBy(SortEnum.WIFI,false);true }
                        R.id.sortByNoiseLevelButton -> { sortBy(SortEnum.VOLUME,false);true }
                        R.id.sortByAirHumidityButton -> { sortBy(SortEnum.HUMIDITY,false);true }
                        else -> false
                    }
                }
                popupMenu.inflate(R.menu.sort_buttons)
                popupMenu.show()
                return true
            }

            R.id.menu_refresh -> {
                refreshData()
                return true
            }

            R.id.menu_about -> {
                startActivity(Intent(applicationContext, AboutActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Sort by parameter + title
    private fun sortBy(sort: SortEnum, isRefresh: Boolean) {
        reversed = if (reversed)
            (sort == sortedBy) && isRefresh
        else (sort == sortedBy) xor isRefresh
        sortedBy = sort

        val newRooms: ArrayList<Room> = when(sort) {
            SortEnum.RECOMMENDATION -> { ArrayList(rooms.sortedWith(compareBy<Room> { if (reversed) it.recommendationLevel else -it.recommendationLevel }.thenBy { it.title })) }
            SortEnum.NAME -> { ArrayList(if (reversed) rooms.sortedWith(compareBy { it.title}).reversed() else rooms.sortedWith(compareBy { it.title})) }
            SortEnum.TEMPERATURE -> { ArrayList(rooms.sortedWith(compareBy<Room> { if (reversed) -it.temperatureHistory.avg else it.temperatureHistory.avg }.thenBy { it.title })) }
            SortEnum.WIFI -> { ArrayList(rooms.sortedWith(compareBy<Room> { if (reversed) it.wifiHistory.avg else -it.wifiHistory.avg }.thenBy { it.title })) }
            SortEnum.VOLUME -> { ArrayList(rooms.sortedWith(compareBy<Room> { if (reversed) -it.noiseHistory.avg else it.noiseHistory.avg }.thenBy { it.title })) }
            SortEnum.HUMIDITY -> { ArrayList(rooms.sortedWith(compareBy<Room> { if (reversed) -it.humidityHistory.avg else it.humidityHistory.avg }.thenBy { it.title })) }
        }

        rooms.clear()
        rooms.addAll(newRooms)

        if (isFiltering)
            filterData(filterString)

        recyclerView.adapter?.notifyDataSetChanged()
    }
    
    private fun filterData(query: String?) : Boolean {
        if (query == null)
            return false

        if (query.isBlank() || query.isEmpty())
            recyclerView.adapter = RoomAdapter(rooms, this)
        else
            recyclerView.adapter = RoomAdapter(ArrayList(rooms.filter { room -> room.title.toUpperCase().contains(query.toUpperCase()) }), this)

        return true
    }

    override fun onResume() {
        super.onResume()
        refreshData()//e.g. Fahrenheit or filtering/weighting could've changed
    }

    private fun refreshData() {
        swipe_refresher.isRefreshing = true

        println("Attempting to fetch JSON")

        val url = "https://stud01.vs.uni-due.de/api/LiveData"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        var isRefreshing = true
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                //parse JSON into list of JSONEntries
                try {
                    entries = Gson().fromJson(body, Array<JSONEntry>::class.java)
                    println("Fetched JSONData successfully")
                } catch(e: Exception) {
                    println("Failed to get JSONData")
                }

                isRefreshing = false
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")

                isRefreshing = false
            }
        })

        //Create filter weighting in the meantime
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val noiseValue = sharedPreferences.getInt(resources.getString(R.string.settings_key_noise),0)
        val noiseWeight = sharedPreferences.getInt(resources.getString(R.string.settings_key_noise_weight),0)
        val tempValue = sharedPreferences.getString(resources.getString(R.string.settings_key_temperature),"")
        val tempWeight = sharedPreferences.getInt(resources.getString(R.string.settings_key_temperature_weight),0)
        val wifiValue = sharedPreferences.getInt(resources.getString(R.string.settings_key_wifi),0)
        val wifiWeight = sharedPreferences.getInt(resources.getString(R.string.settings_key_wifi_weight),0)
        val humidityValue = sharedPreferences.getString(resources.getString(R.string.settings_key_humidity),"")
        val humidityWeight = sharedPreferences.getInt(resources.getString(R.string.settings_key_humidity_weight),0)
        val filterWeightConfiguration = FilterWeightConfiguration(noiseValue, noiseWeight,
                                                                  Pair(RangeBarHelper.getLowValueFromJsonString(tempValue).toInt(),RangeBarHelper.getHighValueFromJsonString(tempValue).toInt()), tempWeight,
                                                                  wifiValue, wifiWeight,
                                                                  Pair(RangeBarHelper.getLowValueFromJsonString(humidityValue).toInt(),RangeBarHelper.getHighValueFromJsonString(humidityValue).toInt()), humidityWeight)
        //save filters for later
        val showCampusB = sharedPreferences.getBoolean(resources.getString(R.string.settings_key_campus_b),true)
        val showCampusL = sharedPreferences.getBoolean(resources.getString(R.string.settings_key_campus_l),true)
        val showCampusM = sharedPreferences.getBoolean(resources.getString(R.string.settings_key_campus_m),true)

        //wait until JSONData received
        while(isRefreshing) { Thread.sleep(100) }

        if(entries != null) {
            rooms.clear()

            for(entry in entries!!) {
                if ((roomFilter.isNullOrEmpty() || entry.Room.toUpperCase().contains(roomFilter.toString().toUpperCase())) && ((showCampusB && entry.Room[0] == 'B') || (showCampusL && entry.Room[0] == 'L') || (showCampusM && entry.Room[0] == 'M'))) {
                    rooms.add(
                        Room(
                            entry.Room,
                            entry.getTemperatureHistory(),
                            entry.getNoiseHistory(),
                            entry.getWifiHistory(),
                            entry.getHumidityHistory(),
                            filterWeightConfiguration
                        )
                    )
                }
            }
        } else println("Entry list is empty")

        Toast.makeText(applicationContext, resources.getString(R.string.toast_data_refreshed), Toast.LENGTH_SHORT).show()

        //Access the RecyclerView Adapter and load JSONData into it
        recyclerView.adapter = RoomAdapter(rooms, this)

        sortBy(sortedBy,true)

        swipe_refresher.isRefreshing = false
    }
}