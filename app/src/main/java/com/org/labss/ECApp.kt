package com.org.labss

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.org.labss.di.AppModule
import com.org.labss.ui.navigation.ECNavHost
import com.org.labss.ui.theme.LabssTheme
import com.org.labss.ui.vm.SharedViewModel

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