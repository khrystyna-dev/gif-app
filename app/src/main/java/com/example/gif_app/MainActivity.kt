package com.example.gif_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.giphy.com/v1/"
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GifsAdapter
    private lateinit var searchView: SearchView
    private lateinit var retroService: DataService
    private var gifs = mutableListOf<DataObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)

        gifs = mutableListOf()
        adapter = GifsAdapter(this, gifs)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter.setOnItemClickListener(object : GifsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, SecondActivity::class.java)

                intent.putExtra("url", gifs[position].images.ogImage.url)
                startActivity(intent)
            }
        })

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retroService = retrofit.create(DataService::class.java)
        retroService.getGifs().enqueue(object : Callback<DataResult?> {
            override fun onResponse(call: Call<DataResult?>, response: Response<DataResult?>) {
                val body = response.body()
                if (body == null) {
                    Log.d(TAG, "No response!")
                }
                gifs.addAll(body!!.res)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<DataResult?>, t: Throwable) {}
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })
    }

    private fun performSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            fetchGifs(retroService.getTrendingGifs())
        } else {
            fetchGifs(retroService.searchGifs(query))
        }
    }

    private fun fetchGifs(call: Call<DataResult>) {
        call.enqueue(object : Callback<DataResult?> {
            override fun onResponse(call: Call<DataResult?>, response: Response<DataResult?>) {
                val body = response.body()
                gifs.clear()
                body?.res?.let {
                    gifs.addAll(it)
                    if (gifs.isEmpty()) {
                        Snackbar.make(recyclerView, "No GIFs found for this request!", Snackbar.LENGTH_LONG).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<DataResult?>, t: Throwable) {
            }
        })
    }
}