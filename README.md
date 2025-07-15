# Pira-TV

Pira-TV is an Android streaming app that brings together movies, TV series and live israeli news channels into one place—serving as a semi-replacement for cables.

---

## Table of Contents

- [Features](#features)  
- [Architecture & Packages](#architecture--packages)  
- [Key Classes & Components](#key-classes--components)  
- [APIs & Services](#apis--services)  
- [UI/UX & Layouts](#uiux--layouts)  
- [Installation & Configuration](#installation--configuration)  
- [Future Plans](#future-plans)  

---

## Features

- **Content browsing**  
  Horizontal carousels & grids for Movies, TV-Series & Channels  
- **Search**  
  Query for movies and TV-series  
- **Filter by Categories**  
  “Action”, “Comedy”, “Trending Now”, etc.  
- **Kids mode**  
  Parental control for avoiding adult content  
- **Favorites**  
  Save favorite movies and TV-series to watch later  
- **Account Management**  
  Sign in / Sign up via Firebase Auth; reset password via email, delete account  

---

## Architecture & Packages

com.example.piratv <br>
├── ui <br>
│ ├── auth # LoginActivity, RegisterActivity, LogoffFragment <br>
│ ├── home # HomeFragment + carousels <br>
│ ├── search # SearchSummaryActivity, SearchResultAdapter <br>
│ ├── previewItem # PreviewItemActivity (play URLs) <br>
│ ├── profile # ProfileFragment (account info, settings) <br>
│ ├── favorites # FavoritesFragment (watchlist) <br>
│ └── settings # App Settings Fragment <br>
├── adapters # RecyclerView adapters <br>
├── models # Data classes: Movie, Series, Channel, FeaturedItem, TitleResult, SearchResponse… <br>
├── apis # Retrofit interfaces (TMDBApi…) <br>
└── MainActivity # Drawer + Navigation host <br>

---

## Key Classes & Components

### `MainActivity`
Hosts the NavHostFragment, drawer menu and FAB to open navigation.

### `HomeFragment`
Displays three carousels for featured movies, series and live channels.

### Carousel Adapters
- **MovieCarouselAdapter**  
- **SeriesCarouselAdapter**  
- **ChannelCarouselAdapter**  
  Populate each carousel and handle item clicks.

### `SearchSummaryActivity`
Fires TMDB/IMDb REST call and shows results in a RecyclerView grid.

### `SearchResultAdapter`
Loads poster images using Glide into grid items.

### `PreviewItemActivity`
Builds and launches the correct embed URL based on content type and source.

### `ProfileFragment`
Displays user email, name, member-since; offers safe-search toggle, password reset, account deletion, and log out.

### `FavoritesFragment`
Placeholder for watchlist; to be extended with local DB or Firebase.

### `SettingsFragment`
App-wide preferences (e.g. parental control).

---

## APIs & Services
TMDB / IMDb REST via Retrofit + Gson

kotlin
Copy
Edit
@GET("search/multi")
fun searchTitles(...): Call<SearchResponse>

@GET("tv/{id}")
fun getSeriesDetails(@Path("id") id: Int): Call<SearchResponseTvSeries>
Authentication
FirebaseAuth for email/password sign-in & user management

Image Loading
Glide

Note: Store your API key in apikeys.properties (ignored by Git) and reference it via BuildConfig.API_KEY_THEMOVIEDB.

---

## UI/UX & Layouts
Material 3 components (Cards, TextInputLayout, SwitchMaterial, ShapeableImageView)

View Binding for type-safe view access

Carousels: Material CarouselLayoutManager

Search Bar at top of HomeFragment

Floating Action Button to open navigation drawer

---

## Installation & Configuration
Clone this repository

Add your AUTH_KEY & API key to the matching variables in apikeys.properties:

properties
Copy
Edit
API_KEY_THEMOVIEDB="YOUR_TMDB_API_KEY"
AUTH_KEY="YOUR_TMDB_AUTH_KEY"
Sync Gradle in Android Studio

Run on an Android device or emulator

Min SDK: 30

Target SDK: 34

---

## Future Plans

Improve recommendations (collaborative filtering)

Ads / billing integration (optional)

---

## Built with ❤️ by Maor Mordo

