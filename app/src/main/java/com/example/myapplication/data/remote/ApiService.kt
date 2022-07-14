package com.example.myapplication.data.remote

import com.example.myapplication.data.model.Movie
import com.example.myapplication.data.model.UserInfoSignIn
import com.example.myapplication.data.model.UserInfoSignUp
import com.example.myapplication.data.remote.response.GetMovieListResponse
import com.example.myapplication.data.remote.response.Response
import com.example.myapplication.data.remote.response.DataResponse
import retrofit2.http.*

interface ApiService {

    @GET("3/discover/movie")
    suspend fun getDiscoverMovie(@QueryMap hashMap: HashMap<String, String> = HashMap()): GetMovieListResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovie(@QueryMap hashMap: HashMap<String, String> = HashMap()): Movie


    @POST("api/v1/consumers/sign_up")
    suspend fun signUp(@Body userInfoSignUp: UserInfoSignUp): Response<DataResponse>


    @POST("api/v1/consumers/sign_in")
    suspend fun signIn(@Body userInfoSignIn: UserInfoSignIn): Response<DataResponse>


    @DELETE("api/v1/consumers/sign_out")
    suspend fun signOut(): retrofit2.Response<Unit>

}
