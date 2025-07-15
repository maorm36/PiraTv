package com.example.piratv.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.piratv.R
import com.example.piratv.models.FeaturedItem
import com.example.piratv.ui.adapters.FeaturedCarouselAdapter
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var recyclerFavoritesMovies: RecyclerView
    private lateinit var recyclerFavoritesSeries: RecyclerView
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFavoritesMovies = view.findViewById(R.id.recyclerFavoritesMovies)
        recyclerFavoritesSeries = view.findViewById(R.id.recyclerFavoritesSeries)

        // 1) Configure carousel layout
        recyclerFavoritesMovies.layoutManager =
            CarouselLayoutManager(MultiBrowseCarouselStrategy())

        recyclerFavoritesSeries.layoutManager =
            CarouselLayoutManager(MultiBrowseCarouselStrategy())

        // 2) Get current user
        val user = auth.currentUser

        // 3) Query Firestore for this user’s TvSeries favorites
        db.collection("users")
            .document(user!!.uid)
            .collection("favoritesTvSeries")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.toObjects(FeaturedItem::class.java)

                if (items.isEmpty()) {
                    /*
                    Toast.makeText(
                        requireContext(),
                        "You haven’t added any TvSeries favorites yet.",
                        Toast.LENGTH_LONG
                    ).show()
                    */
                }
                // 4) Bind to your carousel adapter
                recyclerFavoritesSeries.adapter =
                    FeaturedCarouselAdapter(requireContext(), items)

            }.addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load favorites: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        // 3) Query Firestore for this user’s movie favorites
        db.collection("users")
            .document(user!!.uid)
            .collection("favoritesMovies")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.toObjects(FeaturedItem::class.java)

                if (items.isEmpty()) {
                    /*
                    Toast.makeText(
                        requireContext(),
                        "You haven’t added any movie favorites yet.",
                        Toast.LENGTH_LONG
                    ).show()
                    */
                }
                // 4) Bind to your carousel adapter
                recyclerFavoritesMovies.adapter =
                    FeaturedCarouselAdapter(requireContext(), items)

            }.addOnFailureListener { e ->
                Log.w("myApp", "${e.message}");

                Toast.makeText(
                    requireContext(),
                    "Failed to load favorites: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

    }
}

