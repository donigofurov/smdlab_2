package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie

@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (state) {
        is MovieListUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is MovieListUiState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.message)
                Button(onClick = onRefresh) { Text("Retry") }
            }
        }
        is MovieListUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    TextButton(onClick = onRefresh) { Text("Refresh") }
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = state.favoritesOnly,
                        onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                        label = { Text("Favorites only") }
                    )
                }
                if (state.isFromCache) {
                    Text("Offline — showing saved data", modifier = Modifier.padding(8.dp))
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieItem(movie = movie, onFavoriteClick = { onFavoriteClick(movie.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onFavoriteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(movie.title, fontWeight = FontWeight.Bold)
            Text("${movie.year} · Rating ${movie.rating}")
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Toggle favorite"
            )
        }
    }
}
