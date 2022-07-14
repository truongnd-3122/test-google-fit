package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.response.GetMovieListResponse

interface MovieRepository {

    suspend fun getMovieList(
        hashMap: HashMap<String, String> = HashMap()
    ): GetMovieListResponse

}
