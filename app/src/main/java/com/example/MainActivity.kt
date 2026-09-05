package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CrmRepository
import com.example.data.local.CrmDatabase
import com.example.ui.CrmViewModel
import com.example.ui.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val database = remember { CrmDatabase.getDatabase(applicationContext) }
      val repository = remember { CrmRepository(crmDao = database.crmDao()) }
      val crmViewModel: CrmViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CrmViewModel(repository = repository) as T
          }
        }
      )
      val themeMode by crmViewModel.themeMode.collectAsState()
      MyApplicationTheme(themeMode = themeMode) {
        MainAppScreen(crmViewModel = crmViewModel)
      }
    }
  }
}



