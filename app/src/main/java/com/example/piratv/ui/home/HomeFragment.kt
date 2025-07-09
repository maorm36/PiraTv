package com.example.piratv.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.piratv.ui.adapters.FeaturedCarouselAdapter
import com.example.piratv.databinding.FragmentHomeBinding
import com.example.piratv.models.FeaturedItem
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import android.view.inputmethod.EditorInfo
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.piratv.R
import com.example.piratv.apis.RetrofitClientTheMoviesDB
import com.example.piratv.models.SearchResponse
import com.example.piratv.ui.search.SearchSummaryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val movies = mutableListOf<FeaturedItem>()
    private val series = mutableListOf<FeaturedItem>()
    private val api = RetrofitClientTheMoviesDB.theMoviesDBAPI


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Category buttons
        binding.efabAction.setOnClickListener {
            fetchMovies(genre = 28)
            fetchSeries(genre = 10759)
        }

        binding.efabComedy.setOnClickListener {
            fetchMovies(genre = 35)
            fetchSeries(genre = 35)
        }

        binding.efabCrime.setOnClickListener {
            fetchMovies(genre = 80)
            fetchSeries(genre = 80)
        }

        binding.efabKids.setOnClickListener {
            fetchMovies(genre = 10751)
            fetchSeries(genre = 10762)
        }

        binding.efabAnimation.setOnClickListener {
            fetchMovies(genre = 16)
            fetchSeries(genre = 16)
        }

        binding.efabDrama.setOnClickListener {
            fetchMovies(genre = 18)
            fetchSeries(genre = 18)
        }

        binding.efabDocumentary.setOnClickListener {
            fetchMovies(genre = 99)
            fetchSeries(genre = 99)
        }

        binding.efabTrending.setOnClickListener {
            fetchMovies(10770)
            fetchSeries(10759)
        }

        binding.fabMenuLauncher.setOnClickListener {
            // find the DrawerLayout in the Activity's layout
            val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
            // open it on the "start" side (i.e. left)
            drawer.openDrawer(GravityCompat.START)
        }

        api.getMovies().enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

                val body = response.body()

                // Pull out the full list...
                movies.addAll(
                    body!!.results.map { item ->
                        FeaturedItem(
                            title = item.title,
                            description = item.overview,
                            srcImage = "https://image.tmdb.org/t/p/w500/${item.primaryImage}",
                            id = item.id,
                            type = "movie"
                        )
                    })

                setupCarousel(binding.carouselMovies, movies)

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {}
        })

        api.getSeries().enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

                val body = response.body()

                // Pull out the full list...
                series.addAll(
                    body!!.results.map { item ->
                        FeaturedItem(
                            title = item.name,
                            description = item.overview,
                            srcImage = "https://image.tmdb.org/t/p/w500/${item.primaryImage}",
                            id = item.id,
                            type = "tv"
                        )
                    })

                setupCarousel(binding.carouselSeries, series)

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {}
        })


        val channels = listOf(
            FeaturedItem(
                title = "channel 11",
                description = "News",
                srcImage = "https://yt3.googleusercontent.com/qnEYWQm-pfe8d7B9jwVOsW1rk-sZ5drCg5q9VzVLbM74VP_4IiryVTW-DMPnjdo1qBhFN7WYZ1s=s900-c-k-c0x00ffffff-no-rj",
                id = "11",
                type = "News"
            ),
            FeaturedItem(
                title = "channel 12",
                description = "News",
                srcImage = "https://yt3.googleusercontent.com/e3_gyhbAuwB3mEc0gO3HiImXzi-JHttvoAWiCEVFKm9jLuOEECFLwVe2wIuPHpP8FEIX2NAB=s900-c-k-c0x00ffffff-no-rj",
                id = "12",
                type = "News"
            ),
            FeaturedItem(
                title = "channel 13",
                description = "News",
                srcImage = "https://img.haarets.co.il/bs/0000017f-f826-d887-a7ff-f8e607750000/c2/ec/6904a16e0fd3cc4141b99d57795e/3018302422.jpg?&width=420&height=420&cmsprod",
                id = "13",
                type = "News"
            ),
            FeaturedItem(
                title = "channel 14",
                description = "News",
                srcImage = "https://upload.wikimedia.org/wikipedia/commons/c/c9/Noamperetz-now14logo.png",
                id = "14",
                type = "News"
            ),
            FeaturedItem(
                title = "channel i24",
                description = "News",
                srcImage = "https://upload.wikimedia.org/wikipedia/commons/7/79/LOGO_i24NEWS.png",
                id = "i24",
                type = "News"
            )
        )

        // Handle search action
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text?.toString()?.trim()
                if (!query.isNullOrEmpty()) {
                    val intent = Intent(requireContext(), SearchSummaryActivity::class.java)
                    intent.putExtra("search_query", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }

        setupCarousel(binding.carouselChannels, channels)
    }

    private fun fetchMovies(genre: Int) {
        movies.clear()

        api.getMovies(withGenres = genre).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

                val body = response.body()

                movies.addAll(
                    body!!.results.map { item ->
                        FeaturedItem(
                            title = item.title,
                            description = item.overview,
                            srcImage = "https://image.tmdb.org/t/p/w500/${item.primaryImage}",
                            id = item.id,
                            type = "movie"
                        )
                    })

                setupCarousel(binding.carouselMovies, movies)

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {}
        })
    }

    private fun fetchSeries(genre: Int) {
        series.clear()

        api.getSeries(withGenres = genre).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

                val body = response.body()

                series.addAll(
                    body!!.results.map { item ->
                        FeaturedItem(
                            title = item.name,
                            description = item.overview,
                            srcImage = "https://image.tmdb.org/t/p/w500/${item.primaryImage}",
                            id = item.id,
                            type = "tv"
                        )
                    })

                setupCarousel(binding.carouselSeries, series)

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {}
        })
    }

    private fun setupCarousel(recyclerView: RecyclerView, items: List<FeaturedItem>) {
        recyclerView.adapter = null
        recyclerView.layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())
        recyclerView.adapter = FeaturedCarouselAdapter(requireContext(), items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
