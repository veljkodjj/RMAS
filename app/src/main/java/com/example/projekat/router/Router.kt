package com.example.projekat.router

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aquaspot.screens.UserProfileScreen
import com.example.projekat.data.Resource
import com.example.projekat.model.CustomUser
import com.example.projekat.model.Kladionice
import com.example.projekat.screens.IndexScreen
import com.example.projekat.screens.KladionicaScreen
import com.example.projekat.screens.LoginScreen
import com.example.projekat.screens.RangiranjeScreen
import com.example.projekat.screens.RegisterScreen
import com.example.projekat.screens.SettingScreen
import com.example.projekat.screens.TabelaScreen
import com.example.projekat.viewmodels.AuthViewModel
import com.example.projekat.viewmodels.KladioniceViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(
    viewModel: AuthViewModel,
    kladioniceViewModel : KladioniceViewModel
)
{
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = Routes.loginScreen){
        composable(Routes.loginScreen){
            LoginScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.indexScreen){
            val kladioniceResource = kladioniceViewModel.kladionice.collectAsState()
            val kladionicaMarkers = remember {
                mutableListOf<Kladionice>()
            }
            kladioniceResource.value.let {
                when(it){
                    is Resource.Success -> {
                        kladionicaMarkers.clear()
                        kladionicaMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                    else -> {}
                }
            }
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                kladioniceMarkers = kladionicaMarkers
            )
        }

        composable(Routes.registerScreen){
            RegisterScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")

            val kladioniceResource = kladioniceViewModel.kladionice.collectAsState()
            val kladionicaMarkers = remember {
                mutableListOf<Kladionice>()
            }
            kladioniceResource.value.let {
                when(it){
                    is Resource.Success -> {
                        kladionicaMarkers.clear()
                        kladionicaMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                }
            }

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                kladioniceMarkers = kladionicaMarkers
            )
        }
        composable( //kad se predje sa nekog ekrana na prethodni da zamapmti poziciju
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}/{kladionice}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("beaches") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")
            val kladioniceJson = backStackEntry.arguments?.getString("beaches")
            val kladionice = Gson().fromJson(kladioniceJson, Array<Kladionice>::class.java).toList()

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                kladioniceMarkers = kladionice.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{kladionice}",
            arguments = listOf(
                navArgument("kladionice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val kladioniceJson = backStackEntry.arguments?.getString("kladionice")
            val kladionice = Gson().fromJson(kladioniceJson, Array<Kladionice>::class.java).toList()
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                kladioniceMarkers = kladionice.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(Routes.registerScreen){
            RegisterScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = Routes.kladionicaScreen + "/{kladionica}",
            arguments = listOf(
                navArgument("kladionica"){ type = NavType.StringType }
            )
        ){backStackEntry ->
            val kladionicaJson = backStackEntry.arguments?.getString("kladionica")
            val kladionica = Gson().fromJson(kladionicaJson, Kladionice::class.java)
            kladioniceViewModel.getKladionicaAllRates(kladionica.id)
            KladionicaScreen(
                kladionica = kladionica,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                viewModel = viewModel,
                kladionice = null
            )
        }
        composable(
            route = Routes.kladionicaScreen + "/{kladionica}/{kladionice}",
            arguments = listOf(
                navArgument("kladionica"){ type = NavType.StringType },
                navArgument("kladionice"){ type = NavType.StringType },
            )
        ){backStackEntry ->
            val kladioniceJson = backStackEntry.arguments?.getString("kladionice")
            val kladionice = Gson().fromJson(kladioniceJson, Array<Kladionice>::class.java).toList()
            val kladionicaJson = backStackEntry.arguments?.getString("kladionica")
            val kladionica = Gson().fromJson(kladionicaJson, Kladionice::class.java)
            kladioniceViewModel.getKladionicaAllRates(kladionica.id)

            KladionicaScreen(
                kladionica = kladionica,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel,
                viewModel = viewModel,
                kladionice = kladionice.toMutableList()
            )
        }
        composable(
            route = Routes.userProfileScreen + "/{userData}",
            arguments = listOf(navArgument("userData"){
                type = NavType.StringType
            })
        ){backStackEntry ->
            val userDataJson = backStackEntry.arguments?.getString("userData")
            val userData = Gson().fromJson(userDataJson, CustomUser::class.java)
            val isMy = FirebaseAuth.getInstance().currentUser?.uid == userData.id
            UserProfileScreen(
                navController = navController,
                viewModel = viewModel,
                kladioniceViewModel = kladioniceViewModel,
                userData = userData,
                isMy = isMy
            )
        }
        composable(
            route = Routes.tableScreen + "/{kladionice}",
            arguments = listOf(navArgument("kladionice") { type = NavType.StringType })
        ){ backStackEntry ->
            val kladioniceJson = backStackEntry.arguments?.getString("kladionice")
            val kladionice = Gson().fromJson(kladioniceJson, Array<Kladionice>::class.java).toList()
            TabelaScreen(
                kladionice = kladionice,
                navController = navController,
                kladioniceViewModel = kladioniceViewModel)
        }

        composable(Routes.settingsScreen){
            SettingScreen(navController = navController)
        }
        composable(Routes.rankingScreen){
            RangiranjeScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
