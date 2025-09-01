//package com.refit.app.data.analysis.modelAndView
//
//import android.annotation.SuppressLint
//import androidx.activity.ComponentActivity
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.refit.app.data.analysis.api.AnalysisApi
//import com.refit.app.data.analysis.repository.AnalysisRepositoryImpl
//import com.refit.app.network.RetrofitInstance
//
//@SuppressLint("ContextCastToActivity")
//@Composable
//fun provideAnalysisViewModel() {
//    val api = remember { RetrofitInstance.create(AnalysisApi::class.java) }
//    val repo = remember { AnalysisRepositoryImpl(api) }
//    val activity = LocalContext.current as ComponentActivity
//    val vm: AnalysisViewModel =
//        androidx.lifecycle.viewmodel.compose.viewModel(
//            factory = provideAnalysisViewModel(repo)
//        )
//}