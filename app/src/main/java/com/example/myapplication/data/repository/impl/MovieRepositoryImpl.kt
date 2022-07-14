package jp.co.sgaas.data.repository.impl

import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.response.GetMovieListResponse
import com.example.myapplication.data.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRepository {

    override suspend fun getMovieList(
        hashMap: HashMap<String, String>
    ): GetMovieListResponse {
        return apiService.getDiscoverMovie(hashMap)
    }
}
