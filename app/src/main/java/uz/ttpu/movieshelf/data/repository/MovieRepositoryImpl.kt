package uz.ttpu.movieshelf.data.repository

import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import java.io.IOException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val favorites = local.getFavoriteIds()
        return try {
            val dtos = remote.fetchMovies()
            local.saveMovies(dtos)
            val domainMovies = dtos.map { it.toDomain(favorites.contains(it.id)) }
            MoviesResult(domainMovies, isFromCache = false)
        } catch (e: IOException) {
            val cached = local.getCachedMovies() ?: throw e
            val domainMovies = cached.map { it.toDomain(favorites.contains(it.id)) }
            MoviesResult(domainMovies, isFromCache = true)
        }
    }

    override suspend fun toggleFavorite(movieId: Int): Boolean {
        val favorites = local.getFavoriteIds()
        val isFavorite = favorites.contains(movieId)
        val newFavoriteState = !isFavorite
        local.setFavorite(movieId, newFavoriteState)
        return newFavoriteState
    }
}
