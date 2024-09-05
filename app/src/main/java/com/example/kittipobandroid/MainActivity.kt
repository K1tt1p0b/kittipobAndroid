package com.example.kittipobandroid

import HouseAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var houseAdapter: HouseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        houseAdapter = HouseAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = houseAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_view -> {
                    // Handle 'View' action
                    fetchData()
                    true
                }
                R.id.navigation_add_house -> {
                    // Handle 'Add House Data' action
                    val intent = Intent(this, AddHouse::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = getString(R.string.root_url) + getString(R.string.fetchdata) // Update with your URL
            val request = Request.Builder()
                .url(url)
                .build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d("ResponseBody", responseBody)

                withContext(Dispatchers.Main) {
                    handleFetchResponse(responseBody)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleFetchResponse(responseBody: String) {
        try {
            val houseListType: Type = object : TypeToken<List<House>>() {}.type
            val houses: List<House> = Gson().fromJson(responseBody, houseListType)

            // Update adapter with new data
            houseAdapter = HouseAdapter(houses)
            recyclerView.adapter = houseAdapter
            houseAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
