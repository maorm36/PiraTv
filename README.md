Pira-TV
Pira-TV is an Android streaming app that brings together movies, TV series and live TV channels into one place—serving as a semi-replacement for cables.


Table of Contents
Features

Architecture & Packages

Key Classes & Components

Data Models

APIs & Services

UI/UX & Layouts

Installation & Configuration

Future Plans

License



Features:
Content browsing: Horizontal carousels & grids for Movies, TV-Series & Channels

Search: Query for Movies and TV-Series

Filter by Categories: “Action”, “Comedy”, “Trending Now”, etc.

Kids mode: parental control for avoiding adult content

Favorites: Save titles to watch later

Account Management: Sign in / Sign up via Firebase Auth; reset password, delete account



Architecture & Packages:
com.example.piratv
├── ui
│   ├── auth           # LoginActivity, RegisterActivity, LogoffFragment
│   ├── home           # HomeFragment + carousels
│   ├── search         # SearchSummaryActivity, SearchResultAdapter
│   ├── previewItem    # PreviewItemActivity (play URLs)
│   ├── profile        # ProfileFragment (account info, settings)
│   ├── favorites      # FavoritesFragment (watchlist)
│   └── settings       # App Settings Fragment
├── adapters           # RecyclerView & ViewPager2 adapters
├── models             # Data classes: Movie, Series, Channel, FeaturedItem, TitleResult, SearchResponse…
├── apis               # Retrofit interfaces (ImdbApi, TMDBApi…)
├── binding            # Generated view-binding classes
└── MainActivity       # Drawer + Navigation host
Key Classes & Components
MainActivity
Hosts the NavHostFragment, drawer menu and FAB to open navigation.

HomeFragment
Displays three carousels (ViewPager2 or custom Material carousel) for featured movies, series and live channels.

MovieCarouselAdapter / SeriesCarouselAdapter / ChannelCarouselAdapter
Populate each carousel; handle item clicks via Toast or navigation.

SearchSummaryActivity
Fires TMDB/IMDb REST call and shows results in a RecyclerView grid.

SearchResultAdapter
Loads poster images using Glide into grid items.

PreviewItemActivity
Builds and launches the correct embed URL based on content type and source.

ProfileFragment
Displays user email, name, member-since; offers safe-search toggle, password reset, account deletion and log out.

FavoritesFragment
Placeholder for watchlist; to be extended with local DB or Firebase.

SettingsFragment
(Not shown) for app-wide preferences.

Data Models
kotlin
Copy
Edit
// com.example.piratv.models.Movie
data class Movie(val id: String, val title: String, val imageUrl: String)

// com.example.piratv.models.Series
data class Series(val id: String, val title: String, val imageUrl: String)

// com.example.piratv.models.Channel
data class Channel(val id: String, val title: String, val imageUrl: String)

// com.example.piratv.models.FeaturedItem
data class FeaturedItem(val title: String, val description: String, val srcImage: String, val id: String? = null, val type: String? = null)

// com.example.piratv.models.TitleResult & SearchResponse
data class TitleResult(val id: String, val title: String, val image: String, val year: Int)
data class SearchResponse(val results: List<TitleResult>)
APIs & Services
TMDB / IMDb REST via Retrofit + Gson converter

@GET("search/multi") fun searchTitles(...)

@GET("tv/{id}") fun getSeriesDetails(...)

Authentication: FirebaseAuth for email/password sign-in & user management

Image Loading: Glide

Remember to store your API_KEY in local.properties (ignored by Git) and reference it via BuildConfig.AUTH_KEY.

UI/UX & Layouts
Material 3 components (Cards, TextInputLayout, SwitchMaterial, ShapeableImageView)

View Binding for type-safe view access

Carousels: ViewPager2 with CompositePageTransformer or Material CarouselLayoutManager

Search Bar at top of HomeFragment

Floating Action Button to open navigation drawer

Installation & Configuration
Clone this repository

Add your API key to local.properties:

ini
Copy
Edit
tmdb_api_key="YOUR_TMDB_API_KEY"
Sync Gradle in Android Studio

Run on an Android device or emulator (min SDK …, target SDK …)

Future Plans
Add subtitle support (Hebrew)

Integrate Firebase Realtime/Firestore for watchlists

Improve recommendations (collaborative filtering)

Offline caching of posters & recently watched

Ads / billing integration (optional)

License
This project is released under the MIT License.
Feel free to fork and contribute!

Built with ❤️ by Maor Mordo
