package com.example.dbmstool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dbmstool.navigation.AppNavigation
import com.example.dbmstool.ui.theme.DBMSToolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DBMSToolTheme {
                AppNavigation()
            }
        }
    }
}