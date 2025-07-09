package com.example.piratv.ui.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.piratv.databinding.ActivitySearchSummaryBinding
import com.example.piratv.models.SearchResponse
import com.example.piratv.models.TitleResult
import com.example.piratv.ui.adapters.SearchResultAdapter
import com.example.piratv.ui.previewItem.PreviewItemActivity
import com.example.piratv.apis.RetrofitClientTheMoviesDB
import com.example.piratv.apis.TheMoviesDBAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchSummaryBinding
    private lateinit var api: TheMoviesDBAPI
    private val titles = mutableListOf<TitleResult>()
    private var query: String = ""

    companion object {
        private const val KEY_QUERY = "key_query"
        private const val KEY_TITLES = "key_titles"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api = RetrofitClientTheMoviesDB.theMoviesDBAPI

        // 1) Pull query from Intent
        query = intent.getStringExtra("search_query").orEmpty()
        binding.searchInput.setText(query)
        binding.searchSummaryTitle.text = "Results for “$query”"

        // 2) Restore saved state or perform new search
        if (savedInstanceState != null) {
            // restore query
            query = savedInstanceState.getString(KEY_QUERY, query)
            binding.searchInput.setText(query)
            binding.searchSummaryTitle.text = "Results for “$query”"

            // restore titles list
            val savedList = savedInstanceState.getParcelableArrayList<TitleResult>(KEY_TITLES)
            if (!savedList.isNullOrEmpty()) {
                titles.clear()
                titles.addAll(savedList)
                setupRecycler()
            } else {
                performSearch(query)
            }
        } else {
            performSearch(query)
        }

        // 3) Allow re-search on IME action
        binding.searchInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val newQuery = v.text.toString().trim()
                if (newQuery.isNotEmpty()) {
                    query = newQuery
                    binding.searchSummaryTitle.text = "Results for “$query”"
                    performSearch(query)
                }
                true
            } else false
        }
    }

    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        out.putString(KEY_QUERY, query)
        out.putParcelableArrayList(KEY_TITLES, ArrayList(titles))
    }

    private fun performSearch(query: String) {
        // clear previous
        binding.recyclerSearchResults.adapter = null
        titles.clear()

        val safeSearchForMinors = !getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            .getBoolean("safe_search", false)

        // fetch first 10 pages (as before)
        for (page in 1..10) {
            api.searchTitles(query, page, safeSearchForMinors)
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                        val body = response.body() ?: return
                        titles.addAll(body.results.filter { it.type != "person" })
                        setupRecycler()
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        // you might show an error here
                    }
                })
        }
    }

    private fun setupRecycler() {
        val span = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        binding.recyclerSearchResults.layoutManager = GridLayoutManager(this, span)
        binding.recyclerSearchResults.adapter = SearchResultAdapter(titles) { clicked ->
            startActivity(Intent(this, PreviewItemActivity::class.java).apply {
                putExtra("EXTRA_TITLE", clicked.originalTitle)
                putExtra("EXTRA_TYPE", clicked.type)
                putExtra("EXTRA_ID", clicked.id)
                putExtra("EXTRA_IMAGE_URL", clicked.primaryImage)
                putExtra("EXTRA_DATE", clicked.date)
                putExtra("EXTRA_DESCRIPTION", clicked.overview)
            })
        }
    }
}
