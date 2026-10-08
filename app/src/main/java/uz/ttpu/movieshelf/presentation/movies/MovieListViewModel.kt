package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase
import java.io.IOException

/*
 * 1. Order of calls:
 *    MovieItem (UI) -> MovieListViewModel.onFavoriteClick -> ToggleFavoriteUseCase.invoke -> 
 *    MovieRepositoryImpl.toggleFavorite -> SharedPrefsMovieLocalDataSource.setFavorite
 * 2. Dependencies point inward: 
 *    UI -> ViewModel -> UseCase -> Repository Interface <- Repository Impl -> LocalDataSource.
 *    Repository Impl implements Repository Interface, so its dependency points inward (opposite to call direction).
 * 3. Use case cannot call Log.d because android.util.Log is part of the Android framework. 
 *    The clean way is to define a Logger interface in Domain and implement it in an outer layer.
 */

class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()
    private var favoritesOnly = false

    init { load() }

    fun onRefresh() = load()

    fun onFavoritesOnlyChange(enabled: Boolean) {
        favoritesOnly = enabled
        load()
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            toggleFavorite(movieId)
            load()
        }
    }

    private fun load() {
        _state.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result = if (favoritesOnly) getFavoriteMovies() else getMovies()
                _state.value = MovieListUiState.Success(result.movies, result.isFromCache, favoritesOnly)
            } catch (e: IOException) {
                _state.value = MovieListUiState.Error("Can't load movies. Check your connection and try again.")
            }
        }
    }
}
