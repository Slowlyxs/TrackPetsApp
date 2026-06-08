package com.trackpets.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.trackpets.app.presentation.navigation.TrackPetsNavGraph
import com.trackpets.app.theme.TrackPetsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.trackpets.app.data.datastore.TokenDataStore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackPetsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by tokenDataStore.isLoggedIn().collectAsState(initial = false)
                    val navController = rememberNavController()
                    val startDestination = if (isLoggedIn) "pets" else "login"
                    
                    TrackPetsNavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
