package com.example.piratv.ui.previewItem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.piratv.apis.RetrofitClientTheMoviesDB
import com.example.piratv.apis.TheMoviesDBAPI
import com.example.piratv.databinding.ActivityPreviewItemBinding
import com.example.piratv.models.Comment
import com.example.piratv.models.SearchResponse
import com.example.piratv.models.SearchResponseTvSeries
import com.example.piratv.models.SeasonInfo
import com.example.piratv.models.TitleResult
import com.example.piratv.ui.adapters.CommentsAdapter
import com.example.piratv.ui.adapters.SearchResultAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreviewItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewItemBinding
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var ratingsCol: CollectionReference
    private lateinit var favRef: DocumentReference

    private var seasonsList = mutableListOf<Int>()
    private var episodesList = mutableListOf<Int>()
    private var isFavorite = false
    private var api: TheMoviesDBAPI = RetrofitClientTheMoviesDB.theMoviesDBAPI
    private val Id: String by lazy { intent.getStringExtra("EXTRA_ID").orEmpty() }
    private val title: String by lazy { intent.getStringExtra("EXTRA_TITLE").orEmpty() }
    private val description: String by lazy { intent.getStringExtra("EXTRA_DESCRIPTION").orEmpty() }
    private val imageUrl: String by lazy { intent.getStringExtra("EXTRA_IMAGE_URL").orEmpty() }
    private val type: String by lazy { intent.getStringExtra("EXTRA_TYPE").orEmpty() }
    private var playUrl = ""
    private var sourceSite = "vidsrc.xyz"
    private val servers = listOf("Server 1", "Server 2", "Server 3")
    private var seasons: List<SeasonInfo> = emptyList()

    private fun loadRatings() {
        // Firestore reference for this item's ratings
        val db = FirebaseFirestore.getInstance()
        ratingsCol = db
            .collection("ratings")
            .document(Id)
            .collection("ratings")

    }

    private fun loadTvSeriesDetails() {
        api.getSeriesDetails(Id.toInt())
            .enqueue(object : Callback<SearchResponseTvSeries> {
                override fun onResponse(
                    call: Call<SearchResponseTvSeries>,
                    response: Response<SearchResponseTvSeries>
                ) {
                    seasons = response.body()!!.seasons

                    seasonsList.clear()

                    for (season in seasons) {
                        seasonsList.add(season.seasonNumber)
                    }
                }

                override fun onFailure(call: Call<SearchResponseTvSeries>, t: Throwable) {}
            })
    }

    private fun updateFavoriteButton() {
        binding.buttonFavorite.text =
            if (isFavorite) "unfavorite it"
            else "favorite it"
    }

    private fun loadComments(itemId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .document(itemId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(
                        this,
                        "Error loading comments: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addSnapshotListener
                }

                val list = snapshot
                    ?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Comment::class.java)
                            ?.copy(id = doc.id)
                    }
                    ?: emptyList()

                if (list.isNotEmpty()) {
                    binding.recyclerComments.visibility = View.VISIBLE
                    commentsAdapter.update(list)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewItemBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var episode = 1
        var season = 1
        if (type.equals("tv")) {
            binding.seasonDropdown.visibility = View.VISIBLE
            binding.episodeDropdown.visibility = View.VISIBLE
            binding.seasonMenu.visibility = View.VISIBLE
            binding.episodeMenu.visibility = View.VISIBLE
            binding.chooseSeasonText.visibility = View.VISIBLE
            binding.chooseEpisodeText.visibility = View.VISIBLE

            loadTvSeriesDetails()

            var adapterSeason = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                seasonsList
            )

            var adapterEpisode = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                episodesList
            )

            binding.seasonDropdown.setAdapter(adapterSeason)
            binding.episodeDropdown.setAdapter(adapterEpisode)

            binding.seasonDropdown.setOnItemClickListener { _, _, pos, _ ->
                episodesList.clear()
                season = pos + 1
                for(i in 1.. seasons[pos].episodeCount)
                    episodesList.add(i)
            }

            binding.episodeDropdown.setOnItemClickListener { _, _, pos, _ ->
                episode = pos + 1
            }
        }


        binding.descText.text = description

        if (type.equals("News")) {
            binding.serverDropdown.visibility = View.GONE
            binding.serverMenu.visibility = View.GONE
            binding.invalidServerText.visibility = View.GONE
        }

        // initialize your comments adapter
        commentsAdapter = CommentsAdapter()

        loadRatings()
        val ratingBar = binding.ratingBar
        val avgText = binding.textAverageRating

        // Disable if not signed in
        var user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            ratingBar.isEnabled = false
            avgText.text = "Sign in to rate"
        } else {
            ratingBar.isEnabled = true

            // 1) Load this user's previous rating (if any)
            ratingsCol.document(user.uid).get()
                .addOnSuccessListener { doc ->
                    val previous = (doc.getDouble("rating") ?: 0.0).toFloat()
                    if (previous > 0f) {
                        ratingBar.rating = previous
                    }
                }

            // 2) Compute & display current average
            fun updateAverage() {
                ratingsCol.get()
                    .addOnSuccessListener { snap: QuerySnapshot ->
                        val ratings = snap.documents
                            .mapNotNull { it.getDouble("rating") }
                        val avg = if (ratings.isNotEmpty()) {
                            ratings.average()
                        } else 0.0
                        avgText.text = "Average Rating: ${"%.1f".format(avg)}/5"
                    }
            }
            updateAverage()

            // 3) When user changes rating, write it & refresh average
            ratingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
                if (!fromUser) return@setOnRatingBarChangeListener
                ratingsCol.document(user.uid)
                    .set(mapOf("rating" to newRating))
                    .addOnSuccessListener { updateAverage() }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to submit rating: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        // 1) Show title & poster
        binding.textTitle.text = title
        if (type.equals("News")) {
            Glide.with(binding.imagePoster.context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.imagePoster)
        } else {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + imageUrl)
                .fitCenter()
                .into(binding.imagePoster)
        }

        // 2) Server dropdown
        var adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            servers
        )
        binding.serverDropdown.setAdapter(adapter)
        binding.serverDropdown.setOnItemClickListener { _, _, pos, _ ->
            sourceSite = when (servers[pos]) {
                "Server 1" -> "vidsrc.xyz"
                "Server 2" -> "vidfast.pro"
                "Server 3" -> "vidsrc.net"
                else -> "vidsrc.xyz"
            }
            Toast.makeText(this, "Server set to ${servers[pos]}", Toast.LENGTH_SHORT).show()
        }

        binding.submitCommentBtn.setOnClickListener {
            val text = binding.editTextComment.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val user = FirebaseAuth.getInstance().currentUser ?: run {
                Toast.makeText(this, "Must be signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = mapOf(
                "text" to text,
                "userId" to user.uid,
                "timestamp" to Timestamp.now().toDate()
            )

            FirebaseFirestore.getInstance()
                .collection("comments")
                .document(Id)
                .collection("comments")
                .add(data)
                .addOnSuccessListener {
                    binding.editTextComment.text?.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to post comment: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


        // decide favorites collection
        val db = FirebaseFirestore.getInstance()
        favRef = when (type) {
            "movie" -> db.collection("users")
                .document(user!!.uid)
                .collection("favoritesMovies")
                .document(Id)

            "tv" -> db.collection("users")
                .document(user!!.uid)
                .collection("favoritesTvSeries")
                .document(Id)

            else -> db.collection("users")
                .document(user!!.uid)
                .collection("favoritesOthers")
                .document(Id)
        }

        // 1) check if already favorited
        favRef.get()
            .addOnSuccessListener { snap ->
                isFavorite = snap.exists()
                updateFavoriteButton()
            }
            .addOnFailureListener {
                // ignore, leave as not favorite
                updateFavoriteButton()
            }


        // 3) Favorite
        // 2) set up toggle logic
        binding.buttonFavorite.setOnClickListener {
            val favData = mapOf(
                "id" to Id,
                "title" to title,
                "description" to "",
                "srcImage" to imageUrl,
                "type" to type
            )

            if (isFavorite) {
                // remove from favorites
                favRef.delete()
                    .addOnSuccessListener {
                        isFavorite = false
                        updateFavoriteButton()
                        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to remove: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                // add to favorites
                favRef.set(favData)
                    .addOnSuccessListener {
                        isFavorite = true
                        updateFavoriteButton()
                        Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

        binding.buttonShare.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out “$title” on PiraTV")
            }
            startActivity(Intent.createChooser(share, "Share via"))
        }

        // wire up recyclerComments with a LinearLayoutManager and your CommentsAdapter
        binding.recyclerComments.apply {
            layoutManager = LinearLayoutManager(this@PreviewItemActivity)
            this.adapter =
                commentsAdapter        // this.adapter would also work to be extra explicit
        }

        // then start listening for comments
        loadComments(Id)

        // 5) Play button logic (unchanged)
        binding.buttonStart.setOnClickListener {
            playUrl = when (type) {
                "movie" -> when (sourceSite) {
                    "vidsrc.xyz" -> "https://vidsrc.xyz/embed/movie/$Id"
                    "vidsrc.net" -> "https://vidsrc.net/embed/movie/$Id"
                    "vidfast.pro" -> "https://vidfast.pro/movie/$Id"
                    else -> ""
                }

                "tv" -> when (sourceSite) {
                    "vidsrc.xyz" -> "https://vidsrc.xyz/embed/tv/$Id/${season}/${episode}"
                    "vidsrc.net" -> "https://vidsrc.net/embed/tv/$Id/${season}/${episode}"
                    "vidfast.pro" -> "https://vidfast.pro/tv/$Id/${season}/${episode}"
                    else -> ""
                }

                "News" -> when (title) {
                    "channel 11" -> "https://www.livehdtv.com/yayin/?kanal=156"
                    "channel 12" -> "https://www.livehdtv.com/yayin/?kanal=202"
                    "channel 13" -> "https://www.livehdtv.com/yayin/?kanal=149"
                    "channel 14" -> "https://www.livehdtv.com/yayin/?kanal=201"
                    "channel i24" -> "https://www.livehdtv.com/yayin/?kanal=621"
                    else -> ""
                }

                else -> ""
            }

            if (playUrl.isNotEmpty()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playUrl)))
            } else {
                Toast.makeText(this, "Cannot play this item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
