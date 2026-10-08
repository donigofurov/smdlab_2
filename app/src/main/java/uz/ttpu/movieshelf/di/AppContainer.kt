package uz.ttpu.movieshelf.di

import android.content.Context
import uz.ttpu.movieshelf.data.local.SharedPrefsMovieLocalDataSource
import uz.ttpu.movieshelf.data.remote.FakeMovieRemoteDataSource
import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class AppContainer(context: Context) {
    val fakeRemote = FakeMovieRemoteDataSource()
    private val local = SharedPrefsMovieLocalDataSource(context)
    private val repository = MovieRepositoryImpl(fakeRemote, local)

    val getMovies = GetMoviesUseCase(repository)
    val getFavoriteMovies = GetFavoriteMoviesUseCase(getMovies)
    val toggleFavorite = ToggleFavoriteUseCase(repository)
}
