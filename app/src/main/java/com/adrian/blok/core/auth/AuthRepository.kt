package com.adrian.blok.core.auth

import com.adrian.blok.core.network.ApiClient
import retrofit2.HttpException

class AuthRepository(
    private val tokenStore: TokenStore,
    private val api: AuthApi = ApiClient.create(),
) {
    suspend fun login(email: String, password: String): UserDto {
        val token = api.login(AuthRequest(email, password))
        tokenStore.setToken(token.access_token)
        return api.me()
    }

    suspend fun register(email: String, password: String): UserDto {
        api.register(AuthRequest(email, password))
        return login(email, password)
    }

    suspend fun restoreSession(): UserDto? {
        tokenStore.getToken() ?: return null
        return try {
            api.me()
        } catch (_: HttpException) {
            tokenStore.clear()
            null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun logout() {
        tokenStore.clear()
    }
}
