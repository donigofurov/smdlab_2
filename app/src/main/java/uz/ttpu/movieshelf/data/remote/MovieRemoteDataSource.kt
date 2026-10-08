package uz.ttpu.movieshelf.data.remote

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
}
