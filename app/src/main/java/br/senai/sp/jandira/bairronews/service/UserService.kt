package br.senai.sp.jandira.bairronews.service

import br.senai.sp.jandira.bairronews.model.AuthenticationUser
import br.senai.sp.jandira.bairronews.model.Login
import br.senai.sp.jandira.bairronews.model.NoticiaResponse
import br.senai.sp.jandira.bairronews.model.User
import br.senai.sp.jandira.bairronews.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @Headers("Content-Type: application/json")
    @POST("user")
    fun saveUser(@Body user: User): Call<AuthenticationUser>


    @Headers("Content-Type: application/json")
    @PUT("user/login")
    fun loginUser(@Body login: Login): Call<AuthenticationUser>

    @GET("user/{id}")
    fun dataUser(@Path("id") id: Int): Call<UserResponse>
}


