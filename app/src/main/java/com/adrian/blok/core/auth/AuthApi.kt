package com.adrian.blok.core.auth

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class UserDto(
    val id: Int,
    val email: String,
    val created_at: String,
)

data class TokenDto(
    val access_token: String,
    val token_type: String,
)

data class AuthRequest(
    val email: String,
    val password: String,
)

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body body: AuthRequest): UserDto

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): TokenDto

    @GET("auth/me")
    suspend fun me(): UserDto
}
