package com.org.labss

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.org.labs.di.AppModule
import com.org.labs.ui.navigation.ECNavHost
import com.org.labs.ui.theme.SportTheme
import com.org.labs.ui.vm.SharedViewModel

@Composable
fun ECApp() {
    val context = LocalContext.current
    val vm: SharedViewModel = viewModel(
        factory = SharedViewModel.Factory(
            AppModule.provideProductRepository(context)
        )
    )

    LabssTheme {
        ECNavHost(viewModel = vm)
    }
}