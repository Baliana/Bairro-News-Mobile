package br.senai.sp.jandira.bairronews.service

import br.senai.sp.jandira.bairronews.model.AuthenticationUser
import br.senai.sp.jandira.bairronews.model.Login
import br.senai.sp.jandira.bairronews.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserService {

    @Headers("Content-Type: application/json")
    @POST("user")
    fun saveUser(@Body user: User): Call<AuthenticationUser>


    @Headers("Content-Type: application/json")
    @PUT("user/login")
    fun loginUser(@Body login: Login): Call<AuthenticationUser>
}


