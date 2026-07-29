package com.adrian.blok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adrian.blok.core.auth.AuthRepository
import com.adrian.blok.core.auth.AuthViewModel
import com.adrian.blok.core.auth.LoginScreen
import com.adrian.blok.core.auth.RegisterScreen
import com.adrian.blok.core.auth.TokenStore
import com.adrian.blok.core.network.ApiClient
import com.adrian.blok.modules.notas.data.NotasRepository
import com.adrian.blok.modules.notas.ui.NotasScreen
import com.adrian.blok.modules.notas.viewmodel.NotasViewModel
import com.adrian.blok.ui.theme.BlokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenStore = TokenStore(applicationContext)
        ApiClient.setTokenProvider { tokenStore.cachedToken }

        val authRepository = AuthRepository(tokenStore)
        val notasRepository = NotasRepository()

        setContent {
            BlokTheme {
                BlokApp(
                    authRepository = authRepository,
                    notasRepository = notasRepository,
                )
            }
        }
    }
}

@Composable
private fun BlokApp(
    authRepository: AuthRepository,
    notasRepository: NotasRepository,
) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepository))
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    LaunchedEffect(authState.user, authState.loading) {
        if (authState.loading) return@LaunchedEffect
        val target = if (authState.user == null) "login" else "notas"
        val current = navController.currentDestination?.route
        if (current != target) {
            navController.navigate(target) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    if (authState.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.user == null) "login" else "notas",
    ) {
        composable("login") {
            LoginScreen(
                state = authState,
                onLogin = authViewModel::login,
                onGoRegister = { navController.navigate("register") },
            )
        }
        composable("register") {
            RegisterScreen(
                state = authState,
                onRegister = authViewModel::register,
                onGoLogin = { navController.popBackStack() },
            )
        }
        composable("notas") {
            val user = authState.user ?: return@composable
            val notasViewModel: NotasViewModel = viewModel(
                factory = NotasViewModel.factory(notasRepository),
            )
            val notasState by notasViewModel.state.collectAsStateWithLifecycle()
            NotasScreen(
                userEmail = user.email,
                state = notasState,
                onLogout = authViewModel::logout,
                onCreate = notasViewModel::create,
                onUpdate = notasViewModel::update,
                onDelete = notasViewModel::delete,
            )
        }
    }
}
