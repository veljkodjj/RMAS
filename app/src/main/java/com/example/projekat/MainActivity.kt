package com.example.projekat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projekat.ui.theme.ProjekatTheme
import com.example.projekat.viewmodels.AuthViewModel
import com.example.projekat.viewmodels.AuthViewModelFactory
import com.example.projekat.viewmodels.KladioniceViewModel
import com.example.projekat.viewmodels.KladioniceViewModelFactory

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kladionice(userViewModel, kladioniceViewModel)
        }
    }

    private val userViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }
    private val kladioniceViewModel: KladioniceViewModel by viewModels{
        KladioniceViewModelFactory()
    }
}

