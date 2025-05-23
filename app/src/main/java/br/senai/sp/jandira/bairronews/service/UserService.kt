package br.senai.sp.jandira.bairronews.service


import br.senai.sp.jandira.bairronews.model.Login
import br.senai.sp.jandira.bairronews.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface UserService {
    @POST("user")
    fun saveUser(@Body  character: User): Call<User>
    @PUT("user/login")
    fun loginUser(@Body  character: Login): Call<User>
}